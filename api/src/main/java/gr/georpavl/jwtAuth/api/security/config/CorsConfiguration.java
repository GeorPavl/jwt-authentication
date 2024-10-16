package gr.georpavl.jwtAuth.api.security.config;

import static java.util.Arrays.asList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfiguration {

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    var cors = new org.springframework.web.cors.CorsConfiguration();
    cors.setAllowedOrigins(asList("http://localhost:4200", "http://localhost:80"));
    cors.setAllowedMethods(asList("POST", "GET", "PATCH", "PUT", "HEAD", "DELETE"));
    cors.applyPermitDefaultValues();
    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cors);
    return source;
  }
}
