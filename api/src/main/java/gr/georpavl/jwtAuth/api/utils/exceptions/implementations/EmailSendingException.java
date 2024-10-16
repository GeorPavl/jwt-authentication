package gr.georpavl.jwtAuth.api.utils.exceptions.implementations;

public class EmailSendingException extends RuntimeException {
  public EmailSendingException(String message, Throwable cause) {
    super(message, cause);
  }
}
