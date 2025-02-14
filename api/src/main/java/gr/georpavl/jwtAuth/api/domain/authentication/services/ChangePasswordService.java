package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.ChangePasswordRequest;

public interface ChangePasswordService {
    void changePassword(ChangePasswordRequest request);
}
