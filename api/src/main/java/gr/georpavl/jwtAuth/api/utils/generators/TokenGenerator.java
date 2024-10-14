package gr.georpavl.jwtAuth.api.utils.generators;

import java.util.UUID;

public class TokenGenerator {

  public static String generateToken() {
    return UUID.randomUUID().toString();
  }
}
