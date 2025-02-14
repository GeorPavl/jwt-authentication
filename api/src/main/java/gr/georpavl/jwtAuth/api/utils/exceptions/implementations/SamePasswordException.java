package gr.georpavl.jwtAuth.api.utils.exceptions.implementations;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SamePasswordException extends RuntimeException {
    public SamePasswordException() {
        super("New password must be different from current password");
    }
}
