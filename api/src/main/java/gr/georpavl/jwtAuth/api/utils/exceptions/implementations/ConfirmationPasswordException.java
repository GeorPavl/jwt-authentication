package gr.georpavl.jwtAuth.api.utils.exceptions.implementations;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ConfirmationPasswordException extends RuntimeException {
    public ConfirmationPasswordException() {
        super("New password and confirmation password do not match");
    }
}
