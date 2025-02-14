package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.authentication.dtos.ChangePasswordRequest;
import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import gr.georpavl.jwtAuth.api.security.exceptions.handlers.SecurityExceptionFactory;
import gr.georpavl.jwtAuth.api.security.services.AuthenticatedUserUtilService;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.ConfirmationPasswordException;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.SamePasswordException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangePasswordServiceImpl implements ChangePasswordService {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserUtilService authenticatedUserUtilService;

    @Override
    public void changePassword(ChangePasswordRequest request) {
        try {
            var user = getAuthenticatedUser();
            verifyCurrentPassword(user.getEmail(), request.currentPassword());
            validatePasswordConfirmation(request);
            validatePasswordNotSame(request);
            updateUserPassword(user, request.newPassword());
        } catch (Exception e) {
            throw SecurityExceptionFactory.handleSecurityException(e);
        }
    }

    private User getAuthenticatedUser() {
        var authenticatedUser = authenticatedUserUtilService.getLoggedInUser();
        return findUserOrElseThrow(authenticatedUser.getUsername());
    }

    private void updateUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userJpaRepository.save(user);
    }

    private void verifyCurrentPassword(String email, String currentPassword) {
        var user = findUserOrElseThrow(email);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials provided");
        }
    }

    private void validatePasswordConfirmation(ChangePasswordRequest request) {
        if (!request.newPassword().equals(request.confirmationPassword())) {
            throw new ConfirmationPasswordException();
        }
    }

    private void validatePasswordNotSame(ChangePasswordRequest request) {
        if (request.currentPassword().equals(request.newPassword())) {
            throw new SamePasswordException();
        }
    }

    private User findUserOrElseThrow(String email) {
        return userJpaRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException(String.format("User %s not found", email)));
    }
}
