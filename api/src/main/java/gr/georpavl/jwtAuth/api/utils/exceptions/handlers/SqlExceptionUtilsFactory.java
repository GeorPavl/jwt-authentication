package gr.georpavl.jwtAuth.api.utils.exceptions.handlers;

import gr.georpavl.jwtAuth.api.security.exceptions.implementations.UserAlreadyRegisteredException;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.ResourceAlreadyPresentException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;

@Slf4j
public class SqlExceptionUtilsFactory {

  private static final String UNIQUE_CONSTRAINT_CODE = "23505";
  private static final String FK_CONSTRAINT_CODE = "23503";
  private static final String NOT_NULL_VIOLATION_CODE = "23502";
  private static final String CHECK_CONSTRAINT_VIOLATION_CODE = "23514";
  private static final String STRING_DATA_TRUNCATION_CODE = "22001";
  private static final String INVALID_DATE_FORMAT_CODE = "22007";
  private static final String NUMERIC_VALUE_OUT_OF_RANGE_CODE = "22003";

  private static final Map<String, RuntimeException> exceptionMapping = new HashMap<>();

  static {
    initializeExceptionMappings();
  }

  /**
   * Processes a DataIntegrityViolationException and returns a more specific exception based on SQL
   * state codes.
   *
   * @param exception The DataIntegrityViolationException to process.
   * @return A specific exception based on the SQL state code.
   */
  public static RuntimeException handle(DataIntegrityViolationException exception) {
    var rootCause = exception.getRootCause();

    if (rootCause instanceof SQLException sqlException) {
      var sqlState = sqlException.getSQLState();

      if (isUniqueConstraintViolation(sqlState)) {
        return handleUniqueConstraintViolation(sqlException);
      }

      return getMappedException(sqlState, exception);
    }

    return new DataIntegrityViolationException("Unknown data integrity violation.", exception);
  }

  private static boolean isUniqueConstraintViolation(String sqlState) {
    return UNIQUE_CONSTRAINT_CODE.equals(sqlState);
  }

  private static RuntimeException handleUniqueConstraintViolation(SQLException sqlException) {
    var field = extractDuplicateField(sqlException);

    if ("email".equalsIgnoreCase(field)) {
      return new UserAlreadyRegisteredException("User '" + field + "' is already registered.");
    }

    return new ResourceAlreadyPresentException("Duplicate entry for field: " + field);
  }

  private static RuntimeException getMappedException(
      String sqlState, DataIntegrityViolationException exception) {
    return exceptionMapping.getOrDefault(
        sqlState,
        new DataIntegrityViolationException("Unhandled data integrity violation.", exception));
  }

  private static void initializeExceptionMappings() {
    exceptionMapping.put(
        FK_CONSTRAINT_CODE,
        new DataIntegrityViolationException("Foreign key constraint violation."));
    exceptionMapping.put(
        NOT_NULL_VIOLATION_CODE,
        new DataIntegrityViolationException("Not null constraint violation."));
    exceptionMapping.put(
        CHECK_CONSTRAINT_VIOLATION_CODE,
        new DataIntegrityViolationException("Check constraint violation."));
    exceptionMapping.put(
        STRING_DATA_TRUNCATION_CODE,
        new DataIntegrityViolationException("String data right truncation."));
    exceptionMapping.put(
        INVALID_DATE_FORMAT_CODE, new DataIntegrityViolationException("Invalid date format."));
    exceptionMapping.put(
        NUMERIC_VALUE_OUT_OF_RANGE_CODE,
        new DataIntegrityViolationException("Numeric value out of range."));
  }

  public static String extractDuplicateField(SQLException exception) {
    var message = exception.getMessage();

    if (message != null && message.contains("Key (")) {
      var startIndex = message.indexOf("Key (") + 5;
      var endIndex = message.indexOf(")", startIndex);

      if (startIndex > 0 && endIndex > 0) {
        return message.substring(startIndex, endIndex);
      }
    }

    return "Unknown field";
  }
}
