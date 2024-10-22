package gr.georpavl.jwtAuth.api.domain.users.controllers;

import gr.georpavl.jwtAuth.api.domain.users.dtos.UpdateUserRequest;
import java.util.UUID;

public class UserFixture {

  public static final String USER1_ID = "1e02b0a0-7c92-11e8-adc0-fa7ae01bbebc";
  public static final UUID USER1_UUID = UUID.fromString(USER1_ID);

  public static UpdateUserRequest createUpdateRequest() {
    return UpdateUserRequest.of(
        "updatedEmail@example.com",
        "JohnUpdated",
        "DoeUpdated",
        "111111111"
    );
  }
}
