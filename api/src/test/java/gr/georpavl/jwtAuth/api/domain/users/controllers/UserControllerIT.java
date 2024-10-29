package gr.georpavl.jwtAuth.api.domain.users.controllers;

import static gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture.USER1_ID;
import static gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture.USER1_UUID;
import static gr.georpavl.jwtAuth.api.utils.JwtTokenTestUtil.AUTHORIZATION_HEADER;
import static gr.georpavl.jwtAuth.api.utils.JwtTokenTestUtil.CONTENT_TYPE_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gr.georpavl.jwtAuth.api.domain.users.dtos.UpdateUserRequest;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UserResponse;
import gr.georpavl.jwtAuth.api.domain.users.services.UserService;
import gr.georpavl.jwtAuth.api.domain.users.services.UserUtilsService;
import gr.georpavl.jwtAuth.api.utils.JsonMapperUtil;
import gr.georpavl.jwtAuth.api.utils.JwtTokenTestUtil;
import gr.georpavl.jwtAuth.api.utils.exceptions.CustomErrorResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Slf4j
class UserControllerIT {

  private final String USER_URL_V1 = "/api/v1/users";

  @Autowired private MockMvc mockMvc;
  @Autowired private JsonMapperUtil jsonMapperUtil;
  @SpyBean private UserService userService;
  @SpyBean private UserUtilsService userUtilsService;

  @Test
  void getAll_shouldReturnAllUsersSuccessfully() throws Exception {
    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(USER_URL_V1)
                    .header(AUTHORIZATION_HEADER, JwtTokenTestUtil.createValidUserToken()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var result = jsonMapperUtil.convertJsonToListOfObjects(resultAsString);

    assertEquals(2, result.size());
    verify(userService, times(1)).getAllUsers();
  }

  @Test
  void getById_shouldReturnUserSuccessfully() throws Exception {
    final var url = USER_URL_V1 + "/" + USER1_ID;

    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(url)
                    .header(AUTHORIZATION_HEADER, JwtTokenTestUtil.createValidUserToken()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, UserResponse.class);

    assertEquals(USER1_UUID, result.id());
    verify(userService, times(1)).getUserById(any(UUID.class));
  }

  @Test
  void getById_shouldReturnExceptionForInvalidId() throws Exception {
    final var url = USER_URL_V1 + "/" + UUID.randomUUID();

    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(url)
                    .header(AUTHORIZATION_HEADER, JwtTokenTestUtil.createValidUserToken()))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, CustomErrorResponse.class);

    assertEquals(1, result.getErrors().size());
    verify(userService, times(1)).getUserById(any(UUID.class));
  }

  @Test
  void getById_shouldReturnExceptionForExpiredToken() throws Exception {
    final var url = USER_URL_V1 + "/" + USER1_ID;

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(url)
                .header(AUTHORIZATION_HEADER, JwtTokenTestUtil.createInvalidToken()))
        .andExpect(status().isUnauthorized());

    verify(userService, times(0)).getUserById(any(UUID.class));
  }

  @Test
  void updateUser_shouldUpdateUserSuccessfully() throws Exception {
    final var url = USER_URL_V1 + "/" + USER1_ID;
    var updateRequest = UserFixture.createUpdateRequest();
    var requestBody = jsonMapperUtil.convertToJsonString(updateRequest);

    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch(url)
                    .header(AUTHORIZATION_HEADER, JwtTokenTestUtil.createValidUserToken())
                    .contentType(CONTENT_TYPE_JSON)
                    .content(requestBody))
            .andExpect(status().isAccepted())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, UserResponse.class);

    assertEquals(USER1_UUID, result.id());
    assertEquals(updateRequest.email(), result.email());
    assertEquals(updateRequest.firstName(), result.firstName());
    assertEquals(updateRequest.lastName(), result.lastName());
    assertEquals(updateRequest.phoneNumber(), result.phoneNumber());
    verify(userService, times(1)).updateUser(any(UUID.class), any(UpdateUserRequest.class));
  }

  @Test
  void updateUser_shouldUpdateUserSuccessfullyByAdmin() throws Exception {
    final var url = USER_URL_V1 + "/" + USER1_ID;
    var updateRequest = UserFixture.createUpdateRequest();
    var requestBody = jsonMapperUtil.convertToJsonString(updateRequest);

    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch(url)
                    .header(AUTHORIZATION_HEADER, JwtTokenTestUtil.createValidAdminToken())
                    .contentType(CONTENT_TYPE_JSON)
                    .content(requestBody))
            .andExpect(status().isAccepted())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, UserResponse.class);

    assertEquals(USER1_UUID, result.id());
    assertEquals(updateRequest.email(), result.email());
    assertEquals(updateRequest.firstName(), result.firstName());
    assertEquals(updateRequest.lastName(), result.lastName());
    assertEquals(updateRequest.phoneNumber(), result.phoneNumber());
    verify(userService, times(1)).updateUser(any(UUID.class), any(UpdateUserRequest.class));
  }

  @Test
  void updateUser_shouldThrowExceptionForInvalidId() throws Exception {
    final var url = USER_URL_V1 + "/" + UUID.randomUUID();
    var updateRequest = UserFixture.createUpdateRequest();
    var requestBody = jsonMapperUtil.convertToJsonString(updateRequest);
    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch(url)
                    .header(AUTHORIZATION_HEADER, JwtTokenTestUtil.createValidUserToken())
                    .contentType(CONTENT_TYPE_JSON)
                    .content(requestBody))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse()
            .getContentAsString();

    var result = jsonMapperUtil.convertJsonToObject(resultAsString, CustomErrorResponse.class);

    assertEquals(1, result.getErrors().size());
    verify(userUtilsService, times(0)).checkIfUserIsAdminOrAccountOwner(any(UUID.class));
    verify(userService, times(0)).getUserById(any(UUID.class));
  }

  @Test
  void updateUser_shouldThrowExceptionForInvalidPermission() throws Exception {
    final var adminId = UUID.fromString("1e02b0a0-7c92-11e8-adc0-fa7ae01bbebd");
    final var url = USER_URL_V1 + "/" + adminId;
    var updateRequest = UserFixture.createUpdateRequest();
    var requestBody = jsonMapperUtil.convertToJsonString(updateRequest);
    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch(url)
                    .header(AUTHORIZATION_HEADER, JwtTokenTestUtil.createValidUserToken())
                    .contentType(CONTENT_TYPE_JSON)
                    .content(requestBody))
            .andExpect(status().isForbidden())
            .andReturn()
            .getResponse()
            .getContentAsString();

    var result = jsonMapperUtil.convertJsonToObject(resultAsString, CustomErrorResponse.class);

    assertEquals(1, result.getErrors().size());
    verify(userUtilsService, times(1)).checkIfUserIsAdminOrAccountOwner(any(UUID.class));
    verify(userService, times(0)).getUserById(any(UUID.class));
  }

  @Test
  void updateUser_shouldThrowExceptionForInvalidRequest() throws Exception {
    final var url = USER_URL_V1 + "/" + USER1_UUID;
    var updateRequest = UserFixture.createInvalidUpdateRequest();
    var requestBody = jsonMapperUtil.convertToJsonString(updateRequest);
    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch(url)
                    .header(AUTHORIZATION_HEADER, JwtTokenTestUtil.createValidUserToken())
                    .contentType(CONTENT_TYPE_JSON)
                    .content(requestBody))
            .andExpect(status().isUnprocessableEntity())
            .andReturn()
            .getResponse()
            .getContentAsString();

    var result = jsonMapperUtil.convertJsonToObject(resultAsString, CustomErrorResponse.class);

    assertEquals(4, result.getErrors().size());
    verify(userUtilsService, times(0)).checkIfUserIsAdminOrAccountOwner(any(UUID.class));
    verify(userService, times(0)).getUserById(any(UUID.class));
  }

  @Test
  void deleteUser_shouldDeleteUserSuccessfully() throws Exception {
    final var url = USER_URL_V1 + "/" + USER1_UUID;
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(url)
                .header(AUTHORIZATION_HEADER, JwtTokenTestUtil.createValidAdminToken()))
        .andExpect(status().isNoContent());

    verify(userService, times(1)).deleteUser(any(UUID.class));
  }

  @Test
  void deleteUser_shouldThrowExceptionForInvalidId() throws Exception {
    final var url = USER_URL_V1 + "/" + UUID.randomUUID();
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(url)
                .header(AUTHORIZATION_HEADER, JwtTokenTestUtil.createValidAdminToken()))
        .andExpect(status().isNotFound());

    verify(userService, times(1)).deleteUser(any(UUID.class));
  }

  @Test
  void deleteUser_shouldThrowExceptionForInvalidPermissionIfUserNotAdmin() throws Exception {
    final var url = USER_URL_V1 + "/" + USER1_UUID;
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(url)
                .header(AUTHORIZATION_HEADER, JwtTokenTestUtil.createValidUserToken()))
        .andExpect(status().isForbidden());

    verify(userService, times(0)).deleteUser(any(UUID.class));
  }
}
