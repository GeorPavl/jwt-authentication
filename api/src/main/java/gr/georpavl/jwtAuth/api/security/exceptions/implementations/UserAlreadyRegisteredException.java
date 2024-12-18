package gr.georpavl.jwtAuth.api.security.exceptions.implementations;

public class UserAlreadyRegisteredException extends RuntimeException {

  public UserAlreadyRegisteredException(String userEmail) {
    super(String.format("Email %s already exists", userEmail));
  }
}
