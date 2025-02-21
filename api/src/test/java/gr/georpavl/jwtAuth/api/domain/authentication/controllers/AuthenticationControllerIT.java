package gr.georpavl.jwtAuth.api.domain.authentication.controllers;

import static gr.georpavl.jwtAuth.api.utils.JwtTokenTestUtil.AUTHORIZATION_HEADER;
import static gr.georpavl.jwtAuth.api.utils.JwtTokenTestUtil.CONTENT_TYPE_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gr.georpavl.jwtAuth.api.domain.authentication.AuthenticationFixture;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.ChangePasswordRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RegistrationRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.services.AuthenticationService;
import gr.georpavl.jwtAuth.api.utils.JsonMapperUtil;
import gr.georpavl.jwtAuth.api.utils.JwtTokenTestUtil;
import gr.georpavl.jwtAuth.api.utils.exceptions.CustomErrorResponse;
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
class AuthenticationControllerIT {

  private final String AUTHENTICATION_URL_V1 = "/api/v1/auth";

  @Autowired private MockMvc mockMvc;
  @Autowired private JsonMapperUtil jsonMapperUtil;
  @SpyBean private AuthenticationService authenticationService;

  @Test
  void login_shouldLoginUserSuccessfully() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/login";
    var authenticationRequest = AuthenticationFixture.createAuthenticationRequest();
    var requestBody = jsonMapperUtil.convertToJsonString(authenticationRequest);

    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(url)
                    .contentType(CONTENT_TYPE_JSON)
                    .content(requestBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, AuthenticationResponse.class);

    assertEquals(authenticationRequest.email(), result.userResponse().email());
    assertNotNull(result.accessToken());
    assertNotNull(result.refreshToken());
    verify(authenticationService, times(1)).login(any(AuthenticationRequest.class));
  }

  @Test
  void login_shouldThrowExceptionForInvalidCredentials() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/login";
    var authenticationRequest =
        AuthenticationFixture.createAuthenticationRequestWithInvalidCredentials();
    var requestBody = jsonMapperUtil.convertToJsonString(authenticationRequest);

    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(url)
                    .contentType(CONTENT_TYPE_JSON)
                    .content(requestBody))
            .andExpect(status().isUnauthorized())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, CustomErrorResponse.class);

    assertEquals(1, result.getErrors().size());
    verify(authenticationService, times(1)).login(any(AuthenticationRequest.class));
  }

  @Test
  void login_shouldThrowExceptionForInvalidRequest() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/login";
    var authenticationRequest = AuthenticationFixture.createInvalidAuthenticationRequest();
    var requestBody = jsonMapperUtil.convertToJsonString(authenticationRequest);

    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(url)
                    .contentType(CONTENT_TYPE_JSON)
                    .content(requestBody))
            .andExpect(status().isUnprocessableEntity())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, CustomErrorResponse.class);

    assertEquals(2, result.getErrors().size());
    verify(authenticationService, times(0)).login(any(AuthenticationRequest.class));
  }

  @Test
  void register_shouldRegisterUserSuccessfully() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/register";
    var authenticationRequest = AuthenticationFixture.createRegistrationRequest();
    var requestBody = jsonMapperUtil.convertToJsonString(authenticationRequest);

    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(url)
                    .contentType(CONTENT_TYPE_JSON)
                    .content(requestBody))
            .andExpect(status().isAccepted())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, AuthenticationResponse.class);

    assertEquals(authenticationRequest.email(), result.userResponse().email());
    assertEquals(authenticationRequest.firstName(), result.userResponse().firstName());
    assertEquals(authenticationRequest.lastName(), result.userResponse().lastName());
    assertEquals(authenticationRequest.phoneNumber(), result.userResponse().phoneNumber());
    assertEquals(authenticationRequest.role(), result.userResponse().role());
    verify(authenticationService, times(1)).register(any(RegistrationRequest.class));
  }

  @Test
  void register_shouldThrowExceptionForInvalidRequest() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/register";
    var authenticationRequest = AuthenticationFixture.createInvalidRegistrationRequest();
    var requestBody = jsonMapperUtil.convertToJsonString(authenticationRequest);

    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(url)
                    .contentType(CONTENT_TYPE_JSON)
                    .content(requestBody))
            .andExpect(status().isUnprocessableEntity())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, CustomErrorResponse.class);

    assertEquals(6, result.getErrors().size());
    verify(authenticationService, times(0)).register(any(RegistrationRequest.class));
  }

  @Test
  void register_shouldThrowExceptionForConfirmationPasswordMismatch() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/register";
    var authenticationRequest = AuthenticationFixture.createMismatchedPasswordRegistrationRequest();
    var requestBody = jsonMapperUtil.convertToJsonString(authenticationRequest);

    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(url)
                    .contentType(CONTENT_TYPE_JSON)
                    .content(requestBody))
            .andExpect(status().isInternalServerError())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, CustomErrorResponse.class);

    assertEquals(1, result.getErrors().size());
    verify(authenticationService, times(1)).register(any(RegistrationRequest.class));
  }

  @Test
  void register_shouldThrowExceptionWhenEmailAlreadyExists() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/register";
    var authenticationRequest = AuthenticationFixture.createExistingUserRegistrationRequest();
    var requestBody = jsonMapperUtil.convertToJsonString(authenticationRequest);

    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(url)
                    .contentType(CONTENT_TYPE_JSON)
                    .content(requestBody))
            .andExpect(status().isInternalServerError())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, CustomErrorResponse.class);

    assertEquals(1, result.getErrors().size());
    verify(authenticationService, times(1)).register(any(RegistrationRequest.class));
  }

  @Test
  void register_shouldThrowExceptionWhenPhoneNumberAlreadyExists() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/register";
    var authenticationRequest =
        AuthenticationFixture.createExistingPhoneNumberRegistrationRequest();
    var requestBody = jsonMapperUtil.convertToJsonString(authenticationRequest);

    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(url)
                    .contentType(CONTENT_TYPE_JSON)
                    .content(requestBody))
            .andExpect(status().isConflict())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, CustomErrorResponse.class);

    assertEquals(1, result.getErrors().size());
    verify(authenticationService, times(1)).register(any(RegistrationRequest.class));
  }

  @Test
  void changePassword_shouldChangePasswordSuccessfully() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/change-password";
    var changePasswordRequest = AuthenticationFixture.createValidChangePasswordRequestUser1();
    var requestBody = jsonMapperUtil.convertToJsonString(changePasswordRequest);

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

    assertEquals(AuthenticationFixture.USER1_PASSWORD, changePasswordRequest.currentPassword());
    assertEquals(AuthenticationFixture.NEW_PASSWORD, changePasswordRequest.newPassword());
    verify(authenticationService, times(1)).changePassword(any(ChangePasswordRequest.class));
  }

  @Test
  void changePassword_shouldThrowExceptionWhenCurrentPasswordIsInvalid() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/change-password";
    var changePasswordRequest = AuthenticationFixture.createInvalidPasswordFormatRequest();
    var requestBody = jsonMapperUtil.convertToJsonString(changePasswordRequest);

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

    assertEquals(1, result.getErrors().size());
    assertEquals(
            "Your password must have minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character.",
            result.getErrors().get(0).getMessage());
    verify(authenticationService, never()).changePassword(any(ChangePasswordRequest.class));
  }

  @Test
  void changePassword_shouldThrowExceptionWhenCurrentPasswordIsWrong() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/change-password";
    var changePasswordRequest = AuthenticationFixture.createWrongCurrentPasswordRequest();
    var requestBody = jsonMapperUtil.convertToJsonString(changePasswordRequest);

    var resultAsString =
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.patch(url)
                                    .header(AUTHORIZATION_HEADER, JwtTokenTestUtil.createValidUserToken())
                                    .contentType(CONTENT_TYPE_JSON)
                                    .content(requestBody))
                    .andExpect(status().isUnauthorized())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, CustomErrorResponse.class);

    assertEquals(1, result.getErrors().size());
    assertEquals("Invalid credentials provided.", result.getErrors().get(0).getMessage());
    verify(authenticationService, times(1)).changePassword(any(ChangePasswordRequest.class));
  }

  @Test
  void changePassword_shouldThrowExceptionWhenConfirmationPasswordDoesNotMatch() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/change-password";
    var changePasswordRequest = AuthenticationFixture.createMismatchedConfirmationRequestUser1();
    var requestBody = jsonMapperUtil.convertToJsonString(changePasswordRequest);

    var resultAsString =
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.patch(url)
                                    .header(AUTHORIZATION_HEADER, JwtTokenTestUtil.createValidUserToken())
                                    .contentType(CONTENT_TYPE_JSON)
                                    .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, CustomErrorResponse.class);

    assertEquals(1, result.getErrors().size());
    assertEquals("New password and confirmation password do not match", result.getErrors().get(0).getMessage());
    verify(authenticationService, times(1)).changePassword(any(ChangePasswordRequest.class));
  }

  @Test
  void changePassword_shouldThrowExceptionWhenNewPasswordMatchesCurrentPassword() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/change-password";
    var changePasswordRequest = AuthenticationFixture.createSamePasswordRequestUser1();
    var requestBody = jsonMapperUtil.convertToJsonString(changePasswordRequest);

    var resultAsString =
            mockMvc
                    .perform(
                            MockMvcRequestBuilders.patch(url)
                                    .header(AUTHORIZATION_HEADER, JwtTokenTestUtil.createValidUserToken())
                                    .contentType(CONTENT_TYPE_JSON)
                                    .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, CustomErrorResponse.class);

    assertEquals(1, result.getErrors().size());
    assertEquals("New password must be different from current password", result.getErrors().get(0).getMessage());
    verify(authenticationService, times(1)).changePassword(any(ChangePasswordRequest.class));
  }

  @Test
  void changePassword_shouldThrowExceptionForEmptyCurrentPasswordRequest() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/change-password";
    var changePasswordRequest = new ChangePasswordRequest(null, AuthenticationFixture.NEW_PASSWORD, AuthenticationFixture.NEW_PASSWORD);
    var requestBody = jsonMapperUtil.convertToJsonString(changePasswordRequest);
    var countErrorMessageYourPasswordMustHaveRequirements = 0;
    var countErrorMessageProvideCurrentPassword = 0;
    var countErrorMessageOther = 0;

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
    for (int i = 0; i < result.getErrors().size(); i++) {
      if (result.getErrors().get(i).getMessage().contains("Your password must have minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character.")) {
        countErrorMessageYourPasswordMustHaveRequirements++;
      } else if (result.getErrors().get(i).getMessage().contains("Please provide your current password.")) {
        countErrorMessageProvideCurrentPassword++;
      } else {
        countErrorMessageOther++;
      }
    }

    assertEquals(2, result.getErrors().size());
    assertEquals(1, countErrorMessageYourPasswordMustHaveRequirements);
    assertEquals(1, countErrorMessageProvideCurrentPassword);
    assertEquals(0, countErrorMessageOther);
    verify(authenticationService, never()).changePassword(any(ChangePasswordRequest.class));
  }

  @Test
  void changePassword_shouldThrowExceptionForEmptyPasswordRequest() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/change-password";
    var changePasswordRequest = new ChangePasswordRequest(null, null, null);
    var requestBody = jsonMapperUtil.convertToJsonString(changePasswordRequest);
    var countErrorMessageYourPasswordMustHaveRequirements = 0;
    var countErrorMessageConfirmNewPassword = 0;
    var countErrorMessageProvideNewPassword = 0;
    var countErrorMessageProvideCurrentPassword = 0;
    var countErrorMessageOther = 0;

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
    for (int i = 0; i < result.getErrors().size(); i++) {
      if (result.getErrors().get(i).getMessage().contains("Your password must have minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character.")) {
        countErrorMessageYourPasswordMustHaveRequirements++;
      } else if (result.getErrors().get(i).getMessage().contains("Please confirm your new password.")) {
        countErrorMessageConfirmNewPassword++;
      } else if (result.getErrors().get(i).getMessage().contains("Please provide your new password.")) {
        countErrorMessageProvideNewPassword++;
      } else if (result.getErrors().get(i).getMessage().contains("Please provide your current password.")) {
        countErrorMessageProvideCurrentPassword++;
      } else {
        countErrorMessageOther++;
      }
    }

    assertEquals(6, result.getErrors().size());
    assertEquals(3, countErrorMessageYourPasswordMustHaveRequirements);
    assertEquals(1, countErrorMessageConfirmNewPassword);
    assertEquals(1, countErrorMessageProvideNewPassword);
    assertEquals(1, countErrorMessageProvideCurrentPassword);
    assertEquals(0, countErrorMessageOther);
    verify(authenticationService, never()).changePassword(any(ChangePasswordRequest.class));
  }

  @Test
  void changePassword_shouldThrowExceptionWhenUserNotAuthenticated() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/change-password";
    var changePasswordRequest = AuthenticationFixture.createValidChangePasswordRequestUser1();
    var requestBody = jsonMapperUtil.convertToJsonString(changePasswordRequest);

    mockMvc
            .perform(
                    MockMvcRequestBuilders.patch(url)
                            .contentType(CONTENT_TYPE_JSON)
                            .content(requestBody))
            .andExpect(status().isUnauthorized());

    verify(authenticationService, never()).changePassword(any(ChangePasswordRequest.class));
  }

  @Test
  void changePassword_shouldThrowExceptionForExpiredToken() throws Exception {
    final var url = AUTHENTICATION_URL_V1 + "/change-password";
    var changePasswordRequest = AuthenticationFixture.createValidChangePasswordRequestUser1();
    var requestBody = jsonMapperUtil.convertToJsonString(changePasswordRequest);

    mockMvc
            .perform(
                    MockMvcRequestBuilders.patch(url)
                            .header(AUTHORIZATION_HEADER, JwtTokenTestUtil.createInvalidToken())
                            .contentType(CONTENT_TYPE_JSON)
                            .content(requestBody))
            .andExpect(status().isUnauthorized());

    verify(authenticationService, never()).changePassword(any(ChangePasswordRequest.class));
  }
}
