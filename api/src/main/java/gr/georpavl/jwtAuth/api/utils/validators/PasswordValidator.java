package gr.georpavl.jwtAuth.api.utils.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

  private static final String PASSWORD_PATTERN =
      "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    return !(password == null || !password.matches(PASSWORD_PATTERN));
  }
}
