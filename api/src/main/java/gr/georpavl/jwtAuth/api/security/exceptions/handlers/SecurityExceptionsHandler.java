package gr.georpavl.jwtAuth.api.security.exceptions.handlers;

import gr.georpavl.jwtAuth.api.security.exceptions.implementations.NoPermissionException;
import gr.georpavl.jwtAuth.api.security.exceptions.implementations.UnauthorizedAccessException;
import gr.georpavl.jwtAuth.api.utils.exceptions.CustomErrorResponse;
import gr.georpavl.jwtAuth.api.utils.exceptions.ErrorDetails;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SecurityExceptionsHandler {

  @ExceptionHandler({NoPermissionException.class})
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<CustomErrorResponse> handleException(NoPermissionException exception) {

    var errorDetails = List.of(new ErrorDetails(HttpStatus.UNAUTHORIZED, exception.getMessage()));

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new CustomErrorResponse(errorDetails));
  }

  @ExceptionHandler(UnauthorizedAccessException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<CustomErrorResponse> handleOtherException(UnauthorizedAccessException ex) {
    var errorDetails = List.of(new ErrorDetails(HttpStatus.UNAUTHORIZED, "Unauthorized"));

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new CustomErrorResponse(errorDetails));
  }
}