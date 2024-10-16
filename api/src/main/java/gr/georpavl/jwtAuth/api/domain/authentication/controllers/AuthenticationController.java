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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(
      @RequestBody @Valid AuthenticationRequest request) {
    var result = authenticationService.login(request);
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

  @PatchMapping("/verify")
  public ResponseEntity<Void> verifyUser(
      @RequestParam("token") String token, @RequestParam("code") Integer code) {
    authenticationService.verify(token, code);
    log.info("User is verified successfully.");
    return ResponseEntity.accepted().build();
  }

  @PatchMapping("/resend-verification-email/{userEmail}")
  public ResponseEntity<Void> resendVerificationEmail(@PathVariable("userEmail") String userEmail) {
    authenticationService.resendVerificationEmail(userEmail);
    log.info("Verification mail was sent successfully.");
    return ResponseEntity.accepted().build();
  }
}
