package gr.georpavl.jwtAuth.api.utils.exceptions.implementations;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.CONFLICT)
public class ResourceAlreadyPresentException extends RuntimeException {

  private final String resourceName;
  private final String value;

  public ResourceAlreadyPresentException(String resourceName, String value) {
    super(String.format("%s with value '%s' already presents", resourceName, value));
    this.resourceName = resourceName;
    this.value = value;
  }

  public ResourceAlreadyPresentException(String resourceName) {
    super(String.format("%s already presents", resourceName));
    this.resourceName = resourceName;
    this.value = "";
  }

  public ResourceAlreadyPresentException() {
    super("Resource already presents.");
    this.resourceName = "";
    this.value = "";
  }
}
