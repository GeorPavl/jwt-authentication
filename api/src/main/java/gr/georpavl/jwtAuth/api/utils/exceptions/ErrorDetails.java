package gr.georpavl.jwtAuth.api.utils.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {

  private HttpStatus status;
  private String message;
}
