// package gr.georpavl.jwtAuth.api.utils;
//
// import gr.georpavl.jwtAuth.api.domain.tokens.Token;
// import gr.georpavl.jwtAuth.api.domain.tokens.repositories.TokenJpaRepository;
// import gr.georpavl.jwtAuth.api.domain.users.User;
// import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
// import gr.georpavl.jwtAuth.api.security.services.JwtService;
// import gr.georpavl.jwtAuth.api.security.userDetails.UserDetailsImpl;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.security.Keys;
// import java.util.Base64;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
// import javax.crypto.SecretKey;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
//
// @Service
// @RequiredArgsConstructor
// public class JwtTokenTestUtil {
//
//  private final JwtService jwtService;
//  private final UserJpaRepository userJpaRepository;
//  private final TokenJpaRepository tokenJpaRepository;
//
//  // Use the same secret key as in application.yaml
//  private static final String SECRET_KEY_STRING =
//      "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
//  private static final SecretKey SECRET_KEY =
//      Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY_STRING));
//
//  // JWT Issuer
//  private static final String ISSUER = "jwtAuthApp";
//
//  public  String createValidUserToken() {
//    return createToken("georpavloglou@gmail.com", "USER", 3600000); // 1 hour validity
//  }
//
//  public  String createValidAdminToken() {
//    return createToken("admin@example.com", "ADMIN", 3600000); // 1 hour validity
//  }
//
//  public  String createInvalidToken() {
//    return createToken("invaliduser@example.com", "USER", 3600000); // invalid user
//  }
//
//  public  String createExpiredToken() {
//    return createToken("expireduser@example.com", "USER", -10000); // Already expired
//  }
//
//  private static String generateTOken(String username, long expirationTime) {
//    var user = userJpa
//  }
//
//  private static String createToken(String subject, String role, long expirationTime) {
//    Map<String, Object> claims = new HashMap<>();
//    claims.put("role", role);
//
//    return Jwts.builder()
//        .setClaims(claims)
//        .setSubject(subject)
//        .setIssuer(ISSUER)
//        .setIssuedAt(new Date(System.currentTimeMillis()))
//        .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
//        .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // Use the same key
//        .compact();
//  }
// }

package gr.georpavl.jwtAuth.api.utils;

import gr.georpavl.jwtAuth.api.domain.tokens.Token;
import gr.georpavl.jwtAuth.api.domain.tokens.repositories.TokenJpaRepository;
import gr.georpavl.jwtAuth.api.domain.tokens.services.TokenService;
import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import gr.georpavl.jwtAuth.api.security.services.JwtService;
import gr.georpavl.jwtAuth.api.security.userDetails.UserDetailsImpl;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenTestUtil {

  private final JwtService jwtService;
  private final UserJpaRepository userJpaRepository;
  private final TokenJpaRepository tokenJpaRepository;

//  public final String VALID_USER_TOKEN = createValidUserToken();

  public String createValidUserToken() {
    return createToken("user1@example.com", 3600000); // 1 hour validity
  }

  public String createValidAdminToken() {
    return createToken("admin1@example.com", 3600000); // 1 hour validity
  }

  private String createToken(String username, long expirationTime) {
    var user = userJpaRepository.findByEmail(username).orElseThrow();
    var token = jwtService.generateToken(new UserDetailsImpl(user));
    var savedToken = saveToken(token, user);
    return savedToken.getValue();
  }

  private Token saveToken(String token, User user) {
    var jwtToken = new Token();
    jwtToken.setUser(user);
    jwtToken.setValue(token);
    jwtToken.setExpired(false);
    jwtToken.setRevoked(false);
    return tokenJpaRepository.save(jwtToken);
  }
}
