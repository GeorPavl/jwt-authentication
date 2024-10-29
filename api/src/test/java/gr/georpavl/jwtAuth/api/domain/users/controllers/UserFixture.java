package gr.georpavl.jwtAuth.api.domain.users.controllers;

import gr.georpavl.jwtAuth.api.domain.users.Role;
import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UpdateUserRequest;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UserResponse;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserFixture {

  public static final UUID UUID_DEADBEEF = UUID.fromString("deadbeef-dead-dead-dead-deaddeafbeef");
  public static final String USER1_ID = "1e02b0a0-7c92-11e8-adc0-fa7ae01bbebc";
  public static final UUID USER1_UUID = UUID.fromString(USER1_ID);
  public static final String TEST_USER_MAIL_COM = "test@mail.com";
  public static final String TEST_USER_FIRST_NAME = "Test First Name";
  public static final String TEST_USER_LAST_NAME = "Test Last Name";
  public static final String TEST_USER_PHONE_NUMBER = "111122223";
  public static final String TEST_USER_PASSWORD = "asdf";
  public static final Role TEST_USER_ROLE = Role.USER;
  public static final LocalDateTime NOW = LocalDateTime.now();
  public static final String TEST_TOKEN = "token";
  public static final int TEST_CODE = 123;
  public static final boolean TEST_USER_ENABLED = true;
  public static final boolean TEST_USER_VERIFIED = true;
  public static final String UPDATED_EMAIL_EXAMPLE_COM = "updatedEmail@example.com";
  public static final String UPDATED_FIRST_NAME = "JohnUpdated";
  public static final String UPDATED_LAST_NAME = "DoeUpdated";
  public static final String UPDATED_PHONE_NUMBER = "111111111";

  public static User createTestUser() {
    return User.of(
        UUID_DEADBEEF,
        TEST_USER_MAIL_COM,
        TEST_USER_FIRST_NAME,
        TEST_USER_LAST_NAME,
        TEST_USER_PHONE_NUMBER,
        TEST_USER_PASSWORD,
        TEST_USER_ROLE,
        NOW,
        TEST_USER_ENABLED,
        TEST_USER_VERIFIED,
        NOW,
        NOW,
        TEST_TOKEN,
        TEST_CODE);
  }

  public static UserResponse createTestUserResponse() {
    return UserResponse.of(createTestUser());
  }

  public static UpdateUserRequest createUpdateRequest() {
    return UpdateUserRequest.of(
        UPDATED_EMAIL_EXAMPLE_COM, UPDATED_FIRST_NAME, UPDATED_LAST_NAME, UPDATED_PHONE_NUMBER);
  }

  public static User createUpdatedUser() {
    return User.of(
        UUID_DEADBEEF,
        UPDATED_EMAIL_EXAMPLE_COM,
        UPDATED_FIRST_NAME,
        UPDATED_LAST_NAME,
        UPDATED_PHONE_NUMBER,
        TEST_USER_PASSWORD,
        TEST_USER_ROLE,
        NOW,
        TEST_USER_ENABLED,
        TEST_USER_VERIFIED,
        NOW,
        NOW,
        TEST_TOKEN,
        TEST_CODE);
  }

  public static UpdateUserRequest createInvalidUpdateRequest() {
    return UpdateUserRequest.of("example.com", "J", "D", "1");
  }
}
