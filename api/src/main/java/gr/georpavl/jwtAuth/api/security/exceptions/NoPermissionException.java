package gr.georpavl.jwtAuth.api.security.exceptions;

public class NoPermissionException extends RuntimeException {

  public NoPermissionException(String message) {
    super(message);
  }
}
