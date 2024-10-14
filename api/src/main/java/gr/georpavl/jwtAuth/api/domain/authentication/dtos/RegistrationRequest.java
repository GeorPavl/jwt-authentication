package gr.georpavl.jwtAuth.api.domain.authentication.dtos;

import gr.georpavl.jwtAuth.api.domain.users.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record RegistrationRequest(
    @NotEmpty(message = "'Username' is required.")
        @Size(
            min = 3,
            max = 20,
            message = "'Username' must be between {min} and {max} characters long.")
        String username,
    @NotEmpty(message = "'Email' is required.")
        @Size(
            min = 4,
            max = 50,
            message = "'Email' must be between {min} and {max} characters long.")
        @Email(message = "You have to provide a valid email address.")
        String email,
    @NotEmpty(message = "'Last Name' is required.")
        @Size(min = 3, message = "'First Name must greater than {min} characters long.")
        String firstName,
    @NotEmpty(message = "'Last Name' is required.")
        @Size(min = 3, message = "'Last Name must greater than {min} characters long.")
        String lastName,
    String phoneNumber,
    @NotEmpty(message = "{validations.global.not-empty}")
        @Size(min = 4, max = 120, message = "{validations.global.max-size} 4-120")
        @Pattern(
            regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",
            message =
                "Your password must have minimum eight characters, at least one uppercase letter, one "
                    + "lowercase letter, one number and one special character.")
        String password,
    @NotEmpty(message = "'Role' is required.") Role role) {}
