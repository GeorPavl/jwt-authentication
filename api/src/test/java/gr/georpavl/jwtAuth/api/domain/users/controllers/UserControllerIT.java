package gr.georpavl.jwtAuth.api.domain.users.controllers;

import static gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture.USER1_ID;
import static gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture.USER1_UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.georpavl.jwtAuth.api.domain.tokens.Token;
import gr.georpavl.jwtAuth.api.domain.tokens.repositories.TokenJpaRepository;
import gr.georpavl.jwtAuth.api.domain.users.dtos.UserResponse;
import gr.georpavl.jwtAuth.api.security.filters.JwtAuthenticationFilter;
import gr.georpavl.jwtAuth.api.security.services.JwtService;
import gr.georpavl.jwtAuth.api.utils.JsonMapperUtil;
import gr.georpavl.jwtAuth.api.utils.JwtTokenTestUtil;
import gr.georpavl.jwtAuth.api.utils.exceptions.CustomErrorResponse;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
  @Autowired private ObjectMapper objectMapper;
  @Autowired private JsonMapperUtil jsonMapperUtil;
  @Autowired private JwtTokenTestUtil jwtTokenTestUtil;
  @Mock
  private UserDetailsService userDetailsService;
  @Mock
  private JwtService jwtService;
  @Mock
  private TokenJpaRepository tokenJpaRepository;

  @InjectMocks
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Test
  void getAll_shouldReturnAllUsersSuccessfully() throws Exception {
    final var URL = USER_URL;
    final var myToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnZW9ycGF2bG9nbG91QGdtYWlsLmNvbSIsImlhdCI6MTcyOTQ5ODE5NCwiZXhwIjoxNzI5NTg0NTk0fQ.OrgZMZv5h-KQC3idYQZJja-TkUAe89cQ24DvnPjYq6A";

    String username = "user1";

    // Mock JwtService να επιστρέφει το username από το token
    when(jwtService.extractUsername(myToken)).thenReturn(username);

    // Mock UserDetailsService για να επιστρέψει τα UserDetails
    UserDetails userDetails = User.withUsername(username)
        .password("password")
        .authorities("ROLE_USER")
        .build();
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

    // Δημιουργία έγκυρου JWT token μέσω του jwtService
    String token = jwtService.generateToken(userDetails); // Δημιουργία ενός έγκυρου token με τη μέθοδο δημιουργίας JWT
    // Mock το token repository να επιστρέφει ότι το token είναι έγκυρο και δεν έχει ανακληθεί
    when(tokenJpaRepository.findByValue(token)).thenReturn(Optional.of(new Token(UUID.randomUUID(), token, false, false,
        gr.georpavl.jwtAuth.api.domain.users.User.builder().build())));

    // Mock JwtService για να επιστρέφει true στην εγκυρότητα του token
    when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

    // Εκτέλεση του request με mock token
    var resultAsString =
        mockMvc
            .perform(MockMvcRequestBuilders.get(URL).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    var result = jsonMapperUtil.convertJsonToListOfObjects(resultAsString, UserResponse.class);
    assertEquals(2, result.size());
  }


//  @Test
//  void getById_shouldReturnUserSuccessfully() throws Exception {
//    final var URL = USER_URL + "/" + USER1_ID;
//
//    var resultAsString =
//        mockMvc
//            .perform(MockMvcRequestBuilders.get(URL).header("Authorization", "Bearer " + token))
//            .andExpect(status().isOk())
//            .andReturn()
//            .getResponse()
//            .getContentAsString();
//    var result = jsonMapperUtil.convertJsonToObject(resultAsString, UserResponse.class);
//
//    assertEquals(USER1_UUID, result.id());
//  }
//
//  @Test
//  void getById_shouldReturnExceptionForInvalidId() throws Exception {
//    final var URL = USER_URL + "/" + UUID.randomUUID();
//
//    var resultAsString =
//        mockMvc
//            .perform(MockMvcRequestBuilders.get(URL).header("Authorization", "Bearer " + token))
//            .andExpect(status().isNotFound())
//            .andReturn()
//            .getResponse()
//            .getContentAsString();
//    var result = jsonMapperUtil.convertJsonToObject(resultAsString, CustomErrorResponse.class);
//    log.warn("result {}", result);
//    assertEquals(1, result.getErrors().size());
//  }
}
