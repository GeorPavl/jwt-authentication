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

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenTestUtil {

  public static String createValidUserToken() {
    return createToken("user1@example.com", "USER", 3600000); // 1 hour validity
  }

  public static String createValidAdminToken() {
    return createToken("admin1@example.com", "ADMIN", 3600000); // 1 hour validity
  }

  public static String createInvalidToken() {
    return createToken("user1@example.com", "USER", -3600000);
  }

  private static String createToken(String username, String role, long expirationTime) {
    var claims = getClaims(role);
    var jwt =
        Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    return "Bearer " + jwt;
  }

  private static Map<String, Object> getClaims(String role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role);
    return claims;
  }

  private static Key getSignInKey() {
    var keyBytes =
        Decoders.BASE64.decode("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
