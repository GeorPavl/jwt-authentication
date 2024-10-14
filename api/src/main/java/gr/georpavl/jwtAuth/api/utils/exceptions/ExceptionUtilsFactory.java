package gr.georpavl.jwtAuth.api.utils.exceptions;

import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.ResourceAlreadyPresentException;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;

@Slf4j
public class ExceptionUtilsFactory {

  public static final String UNIQUE_CONSTRAINT_CODE = "23505";
  public static final String FK_CONSTRAINT_CODE = "23503";

  public static RuntimeException of(DataIntegrityViolationException e) {
    var rootCause = e.getRootCause();
    if (rootCause instanceof SQLException sqlException) {
      var sqlState = sqlException.getSQLState();

      if (UNIQUE_CONSTRAINT_CODE.equals(sqlState)) {
        return new ResourceAlreadyPresentException();
      } else if (FK_CONSTRAINT_CODE.equals(sqlState)) {
        return new DataIntegrityViolationException(
            "Entity save failed due to foreign key constraint violation.", e);
      } else {
        return new DataIntegrityViolationException(
            "Entity save failed due to data integrity violation.", e);
      }
    } else {
      throw new DataIntegrityViolationException(
          "Entity save failed due to data integrity violation.", e);
    }
  }
}
