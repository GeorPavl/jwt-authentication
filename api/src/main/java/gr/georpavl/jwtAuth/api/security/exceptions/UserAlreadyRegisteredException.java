package gr.georpavl.jwtAuth.api.security.exceptions;

public class UserAlreadyRegisteredException extends RuntimeException {

  public UserAlreadyRegisteredException(String userEmail) {
    super(String.format("Error during registration process for user %s", userEmail));
  }
}
