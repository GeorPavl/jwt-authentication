package gr.georpavl.jwtAuth.api.security.config;

import java.util.Arrays;
import java.util.List;

public class WhiteListURL {

  protected static final List<String> WHITE_LIST_URLS =
      Arrays.asList(
          "/api/v1/auth/**",
          "/v2/api-docs",
          "/v3/api-docs",
          "/v3/api-docs/**",
          "/swagger-resources",
          "/swagger-resources/**",
          "/configuration/ui",
          "/configuration/security",
          "/swagger-ui/**",
          "/webjars/**",
          "/swagger-ui.html");
}
