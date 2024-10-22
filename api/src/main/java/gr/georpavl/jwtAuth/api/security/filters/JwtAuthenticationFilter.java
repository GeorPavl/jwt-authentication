package gr.georpavl.jwtAuth.api.security.filters;

import gr.georpavl.jwtAuth.api.security.services.JwtService;
import io.jsonwebtoken.ClaimJwtException;
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

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    try {
      var jwt = extractJwtFromRequest(request);
      if (isJwtValidForAuthentication(jwt)) {
        authenticateRequest(jwt, request);
      }
    } catch (ClaimJwtException e) {
      handleJwtException(response, e);
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String extractJwtFromRequest(HttpServletRequest request) {
    final var authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return null;
    }
    return authHeader.substring(7);
  }

  private boolean isJwtValidForAuthentication(String jwt) {
    return jwt != null && SecurityContextHolder.getContext().getAuthentication() == null;
  }

  private void authenticateRequest(String jwt, HttpServletRequest request) {
    var username = jwtService.extractUsername(jwt);
    if (username != null) {
      var userDetails = userDetailsService.loadUserByUsername(username);
      if (jwtService.isTokenValid(jwt, userDetails)) {
        setAuthentication(userDetails, request);
      }
    }
  }

  private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
    var authToken =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }

  private void handleJwtException(HttpServletResponse response, ClaimJwtException e)
      throws IOException {
    log.error("Expired JWT token: {}", e.getMessage());
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token has expired");
  }
}
