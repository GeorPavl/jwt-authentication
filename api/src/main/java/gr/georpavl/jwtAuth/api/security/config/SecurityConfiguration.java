package gr.georpavl.jwtAuth.api.security.config;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import gr.georpavl.jwtAuth.api.domain.users.Role;
import gr.georpavl.jwtAuth.api.security.exceptions.handlers.UserAuthenticationErrorHandler;
import gr.georpavl.jwtAuth.api.security.filters.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@Slf4j
public class SecurityConfiguration {

  private static final String API_V_1_USERS = "/api/v1/users/**";
  private final LogoutHandler logoutHandler;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CorsConfiguration corsConfiguration;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    configureCsrf(http); // Disable CSRF protection for stateless sessions
    configureCors(http); // Configure CORS to allow specific origins and methods
    configureAuthorization(http); // Set up endpoint-specific authorization rules
    configureSessionManagement(http); // Use stateless session management for JWT
    configureExceptionHandling(http); // Handle authentication exceptions
    configureLogout(http); // Define custom logout behavior and handlers
    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  /**
   * Disables CSRF protection as it is not required for stateless JWT-based authentication. CSRF
   * tokens are generally used to prevent cross-site request forgery in stateful sessions, but since
   * JWT stores the session state client-side, CSRF protection is unnecessary here.
   */
  private void configureCsrf(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable);
  }

  /**
   * Configures CORS to allow specific origins and HTTP methods, enabling cross-origin requests from
   * authorized client applications. This is essential for allowing secure access to the API from
   * different origins, especially when the frontend and backend are hosted on different domains.
   */
  private void configureCors(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfiguration.corsConfigurationSource()));
  }

  /**
   * Configures authorization rules for HTTP requests. Allows unrestricted access to a set of
   * publicly accessible URLs, which are specified in the whitelist. Grants access depending on
   * user's role, ensuring that sensitive actions are restricted. Requires authentication for all
   * other requests, securing the application against unauthorized access by default.
   */
  private void configureAuthorization(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
        req ->
            req.requestMatchers(WhiteListURL.WHITE_LIST_URLS.toArray(new String[0]))
                .permitAll() // Allow public access to specific URLs
                .requestMatchers(DELETE, API_V_1_USERS)
                .hasAuthority(Role.ADMIN.name()) // Admin-only access to DELETE on user endpoints
                .anyRequest()
                .authenticated()); // Require authentication for all other requests
  }

  /**
   * Configures session management to be stateless, which is suitable for JWT-based authentication.
   * Stateless sessions mean that all necessary authentication information is stored within the JWT
   * token, reducing server-side resource usage and improving scalability.
   */
  private void configureSessionManagement(HttpSecurity http) throws Exception {
    http.sessionManagement(session -> session.sessionCreationPolicy(STATELESS));
  }

  /**
   * Sets a custom entry point for handling authentication exceptions, ensuring that unauthorized
   * access attempts receive a consistent error response. This approach provides clearer feedback to
   * clients about authentication failures and standardizes error handling.
   */
  private void configureExceptionHandling(HttpSecurity http) throws Exception {
    http.exceptionHandling(
        exception -> exception.authenticationEntryPoint(userAuthenticationErrorHandler()));
  }

  /**
   * Configures custom logout behavior with a specific URL and a handler to revoke tokens properly.
   * Clearing the SecurityContext upon logout ensures that all user-related session data is removed,
   * preventing any session reuse and securing the logout process.
   */
  private void configureLogout(HttpSecurity http) throws Exception {
    http.logout(
        logout ->
            logout
                .logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(
                    (request, response, authentication) -> SecurityContextHolder.clearContext()));
  }

  /**
   * Defines a custom AuthenticationEntryPoint to handle unauthorized access attempts. Sets a realm
   * name for unauthorized responses, which provides additional context to clients about the
   * authentication method being used (in this case, JWT-based). This configuration helps ensure
   * that unauthorized users receive a clear, standardized response, facilitating client-side error
   * handling and improving security transparency.
   *
   * @return A configured UserAuthenticationErrorHandler instance
   */
  @Bean
  public AuthenticationEntryPoint userAuthenticationErrorHandler() {
    var userAuthenticationErrorHandler = new UserAuthenticationErrorHandler();
    userAuthenticationErrorHandler.setRealmName(
        "JWT Authentication"); // Set realm name for unauthorized responses
    return userAuthenticationErrorHandler;
  }
}
