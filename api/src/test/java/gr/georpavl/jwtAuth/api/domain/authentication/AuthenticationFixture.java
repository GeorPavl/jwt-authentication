package gr.georpavl.jwtAuth.api.domain.authentication;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RegistrationRequest;
import gr.georpavl.jwtAuth.api.domain.users.Role;
import gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture;

public class AuthenticationFixture {

  public static final String USER1_USERNAME = "user1@example.com";
  public static final String USER1_PASSWORD = "Password1!";
  public static final String USER1_PHONE_NUMBER = "1234567890";
  public static final String INVALID_USERNAME = "invalidUsername@example.com";
  public static final String INVALID_EMAIL = "invalid.com";
  public static final String INVALID_FIRST_NAME = "F";
  public static final String INVALID_LAST_NAME = "L";
  public static final String INVALID_PHONE_NUMBER = "1";
  public static final String INVALID_PASSWORD = "pass";
  public static final String MISMATCHED_PASSWORD = "Password2!";

  public static final String NEW_USER_EMAIL = "newUser@example.com";
  public static final String NEW_USER_FIRST_NAME = "New";
  public static final String NEW_USER_LAST_NAME = "User";
  public static final String NEW_USER_PHONE_NUMBER = "11112222333";
  public static final String NEW_USER_PASSWORD = "Password1!";
  public static final String NEW_USER_CONFIRMATION_PASSWORD = "Password1!";
  public static final Role NEW_USER_ROLE = Role.USER;

  public static AuthenticationRequest createAuthenticationRequest() {
    return AuthenticationRequest.of(USER1_USERNAME, USER1_PASSWORD);
  }

  public static AuthenticationRequest createAuthenticationRequestWithInvalidCredentials() {
    return AuthenticationRequest.of(INVALID_USERNAME, USER1_PASSWORD);
  }

  public static AuthenticationRequest createInvalidAuthenticationRequest() {
    return AuthenticationRequest.of(INVALID_EMAIL, INVALID_PASSWORD);
  }

  public static RegistrationRequest createRegistrationRequest() {
    return RegistrationRequest.of(
        NEW_USER_EMAIL,
        NEW_USER_FIRST_NAME,
        NEW_USER_LAST_NAME,
        NEW_USER_PHONE_NUMBER,
        NEW_USER_PASSWORD,
        NEW_USER_CONFIRMATION_PASSWORD,
        NEW_USER_ROLE);
  }

  public static RegistrationRequest createInvalidRegistrationRequest() {
    return RegistrationRequest.of(
        INVALID_EMAIL,
        INVALID_FIRST_NAME,
        INVALID_LAST_NAME,
        INVALID_PHONE_NUMBER,
        INVALID_PASSWORD,
        INVALID_PASSWORD,
        NEW_USER_ROLE);
  }

  public static RegistrationRequest createMismatchedPasswordRegistrationRequest() {
    return RegistrationRequest.of(
        NEW_USER_EMAIL,
        NEW_USER_FIRST_NAME,
        NEW_USER_LAST_NAME,
        NEW_USER_PHONE_NUMBER,
        NEW_USER_PASSWORD,
        MISMATCHED_PASSWORD,
        NEW_USER_ROLE);
  }

  public static RegistrationRequest createExistingUserRegistrationRequest() {
    return RegistrationRequest.of(
        USER1_USERNAME,
        NEW_USER_FIRST_NAME,
        NEW_USER_LAST_NAME,
        NEW_USER_PHONE_NUMBER,
        NEW_USER_PASSWORD,
        NEW_USER_CONFIRMATION_PASSWORD,
        NEW_USER_ROLE);
  }

  public static RegistrationRequest createExistingPhoneNumberRegistrationRequest() {
    return RegistrationRequest.of(
        NEW_USER_EMAIL,
        NEW_USER_FIRST_NAME,
        NEW_USER_LAST_NAME,
        USER1_PHONE_NUMBER,
        NEW_USER_PASSWORD,
        NEW_USER_CONFIRMATION_PASSWORD,
        NEW_USER_ROLE);
  }

  public static AuthenticationResponse createAuthenticationResponse() {
    return AuthenticationResponse.of(UserFixture.createTestUserResponse(), "", "");
  }
}
