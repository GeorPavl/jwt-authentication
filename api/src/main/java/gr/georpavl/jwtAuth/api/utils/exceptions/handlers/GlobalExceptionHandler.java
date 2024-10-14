package gr.georpavl.jwtAuth.api.utils.exceptions.handlers;

import gr.georpavl.jwtAuth.api.utils.exceptions.CustomErrorResponse;
import gr.georpavl.jwtAuth.api.utils.exceptions.ErrorDetails;
import gr.georpavl.jwtAuth.api.utils.exceptions.ExceptionUtilsFactory;
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

  @ExceptionHandler({MethodArgumentNotValidException.class})
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ResponseEntity<CustomErrorResponse> handelMethodNotValidException(
      MethodArgumentNotValidException e) {
    var fieldErrors = e.getBindingResult().getFieldErrors();
    var errorDetails = new ArrayList<ErrorDetails>();
    for (var error : fieldErrors) {
      errorDetails.add(
          new ErrorDetails(HttpStatus.UNPROCESSABLE_ENTITY, error.getDefaultMessage()));
    }
    return ResponseEntity.unprocessableEntity().body(new CustomErrorResponse(errorDetails));
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<CustomErrorResponse> handleOtherException(Exception e) {
    var errorDetails =
        List.of(new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, extractErrorMessage(e)));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new CustomErrorResponse(errorDetails));
  }

  @ExceptionHandler({ResourceNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<CustomErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException e) {
    var errorDetails = List.of(new ErrorDetails(HttpStatus.NOT_FOUND, extractErrorMessage(e)));
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomErrorResponse(errorDetails));
  }

  @ExceptionHandler({ResourceAlreadyPresentException.class})
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<CustomErrorResponse> handleResourceAlreadyExistsException(
      ResourceAlreadyPresentException e) {
    var errorDetails = List.of(new ErrorDetails(HttpStatus.CONFLICT, extractErrorMessage(e)));
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new CustomErrorResponse(errorDetails));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<CustomErrorResponse> handleDataIntegrityViolationException(
      DataIntegrityViolationException e) {
    var translatedException = ExceptionUtilsFactory.of(e);
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
