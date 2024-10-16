package gr.georpavl.jwtAuth.api.utils.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
  String message() default
      "Your password must have minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
