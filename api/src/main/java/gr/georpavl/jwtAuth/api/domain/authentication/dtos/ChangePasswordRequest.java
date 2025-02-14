package gr.georpavl.jwtAuth.api.domain.authentication.dtos;

import gr.georpavl.jwtAuth.api.utils.validators.ValidPassword;
import lombok.Builder;
import jakarta.validation.constraints.NotEmpty;

@Builder
public record ChangePasswordRequest(
        @NotEmpty(message = "Please provide your current password.")
        @ValidPassword
        String currentPassword,

        @NotEmpty(message = "Please provide your new password.")
        @ValidPassword
        String newPassword,

        @NotEmpty(message = "Please confirm your new password.")
        @ValidPassword
        String confirmationPassword) {

    public static ChangePasswordRequest of(
            String currentPassword,
            String newPassword,
            String confirmationPassword) {
        return ChangePasswordRequest.builder()
                .currentPassword(currentPassword)
                .newPassword(newPassword)
                .confirmationPassword(confirmationPassword)
                .build();
    }
}
