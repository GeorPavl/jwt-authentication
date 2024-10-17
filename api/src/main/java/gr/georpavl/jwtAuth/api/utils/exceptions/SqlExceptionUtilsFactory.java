package gr.georpavl.jwtAuth.api.utils.exceptions;

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

  public static RuntimeException of(DataIntegrityViolationException e) {
    var rootCause = e.getRootCause();

    if (rootCause instanceof SQLException sqlException) {
      var sqlState = sqlException.getSQLState();
      if (exceptionMapping.containsKey(sqlState)) {
        return exceptionMapping.get(sqlState);
      } else {
        return new DataIntegrityViolationException("Unhandled data integrity violation.", e);
      }
    } else {
      return new DataIntegrityViolationException("Unknown data integrity violation.", e);
    }
  }

  private static void initializeExceptionMappings() {
    addExceptionMapping(
        UNIQUE_CONSTRAINT_CODE,
        new ResourceAlreadyPresentException("Unique constraint violation."));
    addExceptionMapping(
        FK_CONSTRAINT_CODE,
        new DataIntegrityViolationException("Foreign key constraint violation."));
    addExceptionMapping(
        NOT_NULL_VIOLATION_CODE,
        new DataIntegrityViolationException("Not null constraint violation."));
    addExceptionMapping(
        CHECK_CONSTRAINT_VIOLATION_CODE,
        new DataIntegrityViolationException("Check constraint violation."));
    addExceptionMapping(
        STRING_DATA_TRUNCATION_CODE,
        new DataIntegrityViolationException("String data right truncation."));
    addExceptionMapping(
        INVALID_DATE_FORMAT_CODE, new DataIntegrityViolationException("Invalid date format."));
    addExceptionMapping(
        NUMERIC_VALUE_OUT_OF_RANGE_CODE,
        new DataIntegrityViolationException("Numeric value out of range."));
  }

  public static void addExceptionMapping(String sqlStateCode, RuntimeException exception) {
    exceptionMapping.put(sqlStateCode, exception);
  }
}
