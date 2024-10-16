package gr.georpavl.jwtAuth.api.domain.authentication.controllers;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.AuthenticationResponse;
import gr.georpavl.jwtAuth.api.domain.authentication.dtos.RegistrationRequest;
import gr.georpavl.jwtAuth.api.domain.authentication.services.AuthenticationService;
import gr.georpavl.jwtAuth.api.domain.authentication.services.TokenManagerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

  private final AuthenticationService authenticationService;
  private final TokenManagerService tokenManagerService;

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody @Valid AuthenticationRequest request) {
    var result = authenticationService.authenticate(request);
    log.info("User is authenticated successfully.");
    return ResponseEntity.ok(result);
  }

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @Valid @RequestBody RegistrationRequest request) {
    var result = authenticationService.register(request);
    log.info("User is registered successfully.");
    return ResponseEntity.accepted().body(result);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request) {
    var result = tokenManagerService.refreshToken(request);
    log.info("Token is refreshed successfully.");
    return ResponseEntity.ok(result);
  }

  @GetMapping("/verify")
  public ResponseEntity<Void> verifyUser(
      @RequestParam("token") String token, @RequestParam("code") Integer code) {
    authenticationService.verify(token, code);
    return ResponseEntity.ok().build();
  }
}
