package gr.georpavl.jwtAuth.api.utils.exceptions.implementations;

public class PasswordMissMatchException extends RuntimeException {
  public PasswordMissMatchException() {
    super("Confirmation password doesn't match");
  }
}
