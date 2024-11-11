package gr.georpavl.jwtAuth.api.domain.authentication.controllers;

import static gr.georpavl.jwtAuth.api.utils.JwtTokenTestUtil.CONTENT_TYPE_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gr.georpavl.jwtAuth.api.domain.authentication.AuthenticationFixture;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RegistrationRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.services.AuthenticationService;
import gr.georpavl.jwtAuth.api.utils.JsonMapperUtil;
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
}
