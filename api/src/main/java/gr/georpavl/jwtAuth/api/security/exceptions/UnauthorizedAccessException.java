package gr.georpavl.jwtAuth.api.security.exceptions;

public class UnauthorizedAccessException extends RuntimeException {

  public UnauthorizedAccessException(String message) {
    super(message);
  }
}
