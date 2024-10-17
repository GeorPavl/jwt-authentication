package gr.georpavl.jwtAuth.api.utils.exceptions.handlers;

import gr.georpavl.jwtAuth.api.utils.exceptions.CustomErrorResponse;
import gr.georpavl.jwtAuth.api.utils.exceptions.ErrorDetails;
import gr.georpavl.jwtAuth.api.utils.exceptions.SqlExceptionUtilsFactory;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.PasswordMissMatchException;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.ResourceAlreadyPresentException;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<CustomErrorResponse> handleOtherException(Exception e) {
    log.error("Unexpected error occurred: {}", e.getMessage(), e);
    var errorDetails =
        List.of(new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, extractErrorMessage(e)));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new CustomErrorResponse(errorDetails));
  }

  @ExceptionHandler({MethodArgumentNotValidException.class})
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ResponseEntity<CustomErrorResponse> handelMethodNotValidException(
      MethodArgumentNotValidException e) {
    log.error("Validation error: {}", e.getMessage(), e);
    var fieldErrors = e.getBindingResult().getFieldErrors();
    var errorDetails = new ArrayList<ErrorDetails>();
    for (var error : fieldErrors) {
      errorDetails.add(
          new ErrorDetails(HttpStatus.UNPROCESSABLE_ENTITY, error.getDefaultMessage()));
    }
    return ResponseEntity.unprocessableEntity().body(new CustomErrorResponse(errorDetails));
  }

  @ExceptionHandler({ResourceNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<CustomErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException e) {
    log.error("Resource not found: {}", e.getMessage(), e);
    var errorDetails = List.of(new ErrorDetails(HttpStatus.NOT_FOUND, extractErrorMessage(e)));
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomErrorResponse(errorDetails));
  }

  @ExceptionHandler({ResourceAlreadyPresentException.class})
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<CustomErrorResponse> handleResourceAlreadyExistsException(
      ResourceAlreadyPresentException e) {
    log.error("Resource conflict: {}", e.getMessage(), e);
    var errorDetails = List.of(new ErrorDetails(HttpStatus.CONFLICT, extractErrorMessage(e)));
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new CustomErrorResponse(errorDetails));
  }

  @ExceptionHandler(PasswordMissMatchException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<CustomErrorResponse> handlePasswordMissMatchException(
      PasswordMissMatchException e) {
    log.error("Password mismatch error: {}", e.getMessage(), e);
    var errorDetails =
        List.of(new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, extractErrorMessage(e)));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new CustomErrorResponse(errorDetails));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<CustomErrorResponse> handleDataIntegrityViolationException(
      DataIntegrityViolationException e) {
    var translatedException = SqlExceptionUtilsFactory.of(e);
    log.error("Data integrity violation: {}", translatedException.getMessage(), e);
    var errorDetails =
        List.of(new ErrorDetails(HttpStatus.CONFLICT, extractErrorMessage(translatedException)));
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new CustomErrorResponse(errorDetails));
  }

  private String extractErrorMessage(Exception e) {
    String message = e.getMessage();
    if (message != null && message.contains("Detail:")) {
      message = message.split("Detail:")[0].trim();
    }
    return message;
  }
}
