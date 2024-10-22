package gr.georpavl.jwtAuth.api.domain.users.controllers;

import static gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture.USER1_ID;
import static gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture.USER1_UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gr.georpavl.jwtAuth.api.domain.users.dtos.UserResponse;
import gr.georpavl.jwtAuth.api.utils.JsonMapperUtil;
import gr.georpavl.jwtAuth.api.utils.JwtTokenTestUtil;
import gr.georpavl.jwtAuth.api.utils.exceptions.CustomErrorResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
class UserControllerIT {

  private final String USER_URL = "/api/v1/users";

  @Autowired private MockMvc mockMvc;
  @Autowired private JsonMapperUtil jsonMapperUtil;

  @Test
  void getAll_shouldReturnAllUsersSuccessfully() throws Exception {
    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(USER_URL)
                    .header("Authorization", JwtTokenTestUtil.createValidUserToken()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var result = jsonMapperUtil.convertJsonToListOfObjects(resultAsString, UserResponse.class);

    assertEquals(2, result.size());
  }

  @Test
  void getById_shouldReturnUserSuccessfully() throws Exception {
    final var URL = USER_URL + "/" + USER1_ID;

    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(URL)
                    .header("Authorization", JwtTokenTestUtil.createValidUserToken()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, UserResponse.class);

    assertEquals(USER1_UUID, result.id());
  }

  @Test
  void getById_shouldReturnExceptionForInvalidId() throws Exception {
    final var URL = USER_URL + "/" + UUID.randomUUID();

    var resultAsString =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(URL)
                    .header("Authorization", JwtTokenTestUtil.createValidUserToken()))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var result = jsonMapperUtil.convertJsonToObject(resultAsString, CustomErrorResponse.class);

    assertEquals(1, result.getErrors().size());
  }

  @Test
  void getById_shouldReturnExceptionForIExpiredToken() throws Exception {
    final var URL = USER_URL + "/" + USER1_ID;

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(URL)
                .header("Authorization", JwtTokenTestUtil.createInvalidToken()))
        .andExpect(status().isUnauthorized());
  }
}
