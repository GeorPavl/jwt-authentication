package gr.georpavl.jwtAuth.api.security.filters;

import gr.georpavl.jwtAuth.api.domain.tokens.repositories.TokenJpaRepository;
import gr.georpavl.jwtAuth.api.security.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenJpaRepository tokenJpaRepository;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    if (isAuthEndpoint(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    final String jwt = extractJwtFromRequest(request);
    if (jwt == null) {
      filterChain.doFilter(request, response);
      return;
    }

    final String userEmail = jwtService.extractUsername(jwt);
    if (userEmail == null || isUserAlreadyAuthenticated()) {
      filterChain.doFilter(request, response);
      return;
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
    if (isTokenValid(jwt, userDetails)) {
      setAuthentication(userDetails, request);
    }

    filterChain.doFilter(request, response);
  }

  private boolean isAuthEndpoint(HttpServletRequest request) {
    return request.getServletPath().contains("/api/v1/auth");
  }

  private String extractJwtFromRequest(HttpServletRequest request) {
    final String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return null;
    }
    return authHeader.substring(7);
  }

  private boolean isUserAlreadyAuthenticated() {
    return SecurityContextHolder.getContext().getAuthentication() != null;
  }

  private boolean isTokenValid(String jwt, UserDetails userDetails) {
    boolean tokenExistsAndValid =
        tokenJpaRepository
            .findByValue(jwt)
            .map(t -> !t.isExpired() && !t.isRevoked())
            .orElse(false);
    return jwtService.isTokenValid(jwt, userDetails) && tokenExistsAndValid;
  }

  private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }
}
