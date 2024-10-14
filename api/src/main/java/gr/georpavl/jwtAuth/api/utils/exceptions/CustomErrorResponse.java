package gr.georpavl.jwtAuth.api.utils.exceptions;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomErrorResponse {

  private List<ErrorDetails> errors;
}
