package gr.georpavl.jwtAuth.api.security.exceptions;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static org.zalando.problem.Status.FORBIDDEN;
import static org.zalando.problem.Status.UNAUTHORIZED;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.StatusType;

@SuppressWarnings("unused")
@JsonInclude(NON_EMPTY)
@JsonIgnoreProperties({"stackTrace", "type", "title", "message", "localizedMessage", "parameters"})
public class CommonSecurityException extends AbstractThrowableProblem {

  private CommonSecurityException(StatusType status, String detail) {
    super(null, null, status, detail, null, null, null);
  }

  public static CommonSecurityException unauthorized() {
    return new CommonSecurityException(UNAUTHORIZED, "Unauthorized or Bad Credentials");
  }

  public static CommonSecurityException forbidden() {
    return new CommonSecurityException(FORBIDDEN, "Forbidden");
  }

  public static CommonSecurityException headerError() {
    return new CommonSecurityException(FORBIDDEN, "Missing header");
  }
}
