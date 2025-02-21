package gr.georpavl.jwtAuth.api.domain.authentication.services;

import gr.georpavl.jwtAuth.api.domain.authentication.AuthenticationFixture;
import gr.georpavl.jwtAuth.api.domain.users.User;
import gr.georpavl.jwtAuth.api.domain.users.controllers.UserFixture;
import gr.georpavl.jwtAuth.api.domain.users.repositories.UserJpaRepository;
import gr.georpavl.jwtAuth.api.security.exceptions.implementations.UnauthorizedAccessException;
import gr.georpavl.jwtAuth.api.security.services.AuthenticatedUserUtilService;
import gr.georpavl.jwtAuth.api.security.userDetails.UserDetailsImpl;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.ConfirmationPasswordException;
import gr.georpavl.jwtAuth.api.utils.exceptions.implementations.SamePasswordException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class ChangePasswordServiceImplTest {

    @InjectMocks
    private ChangePasswordServiceImpl changePasswordService;
    @Mock
    private AuthenticatedUserUtilService authenticatedUserUtilService;
    @Mock
    private UserJpaRepository userJpaRepository;
    @Mock
    private PasswordEncoder passwordEncoder;


    @Test
    void changePassword_shouldChangePasswordSuccessfully() {
        var request = AuthenticationFixture.createValidChangePasswordRequest();
        var testUser = UserFixture.createTestUser();
        testUser.setPassword(AuthenticationFixture.CURRENT_PASSWORD);
        var testUserDetails = new UserDetailsImpl(testUser);
        String currentEncodedPassword = testUser.getPassword();
        String newEncodedPassword = AuthenticationFixture.NEW_PASSWORD;

        when(authenticatedUserUtilService.getLoggedInUser())
                .thenReturn(testUserDetails);
        when(userJpaRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.currentPassword(), currentEncodedPassword))
                .thenReturn(true);
        when(passwordEncoder.encode(request.newPassword()))
                .thenReturn(newEncodedPassword);

        assertEquals(request.currentPassword(), testUser.getPassword());
        assertDoesNotThrow(
                () -> changePasswordService.changePassword(request));
        assertEquals(request.newPassword(), testUser.getPassword());

        verify(authenticatedUserUtilService).getLoggedInUser();
        verify(userJpaRepository, times(2)).findByEmail(testUser.getEmail());
        verify(passwordEncoder).matches(request.currentPassword(), currentEncodedPassword);
        verify(passwordEncoder).encode(request.newPassword());
        verify(userJpaRepository).save(testUser);
    }

    @Test
    void changePassword_shouldThrowExceptionWhenCurrentPasswordIsInvalid() {
        var request = AuthenticationFixture.createInvalidPasswordFormatRequest();
        var testUser = UserFixture.createTestUser();
        testUser.setPassword(AuthenticationFixture.CURRENT_PASSWORD);
        var testUserDetails = new UserDetailsImpl(testUser);

        when(authenticatedUserUtilService.getLoggedInUser())
                .thenReturn(testUserDetails);
        when(userJpaRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.currentPassword(), testUser.getPassword()))
                .thenReturn(false);

        assertNotEquals(request.currentPassword(), testUser.getPassword());
        assertThrows(
                UnauthorizedAccessException.class,
                () -> changePasswordService.changePassword(request));

        verify(authenticatedUserUtilService).getLoggedInUser();
        verify(userJpaRepository, times(2)).findByEmail(testUser.getEmail());
        verify(passwordEncoder).matches(request.currentPassword(), testUser.getPassword());
        verify(userJpaRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_shouldThrowExceptionWhenCurrentPasswordIsWrong() {
        var request = AuthenticationFixture.createWrongCurrentPasswordRequest();
        var testUser = UserFixture.createTestUser();
        testUser.setPassword(AuthenticationFixture.CURRENT_PASSWORD);
        var testUserDetails = new UserDetailsImpl(testUser);

        when(authenticatedUserUtilService.getLoggedInUser())
                .thenReturn(testUserDetails);
        when(userJpaRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.currentPassword(), testUser.getPassword()))
                .thenReturn(false);

        assertNotEquals(request.currentPassword(), testUser.getPassword());
        assertThrows(
                UnauthorizedAccessException.class,
                () -> changePasswordService.changePassword(request));

        verify(authenticatedUserUtilService).getLoggedInUser();
        verify(userJpaRepository, times(2)).findByEmail(testUser.getEmail());
        verify(passwordEncoder).matches(request.currentPassword(), testUser.getPassword());
        verify(userJpaRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_shouldThrowExceptionWhenConfirmationPasswordDoesNotMatch() {
        var request = AuthenticationFixture.createMismatchedConfirmationRequest();
        var testUser = UserFixture.createTestUser();
        testUser.setPassword(AuthenticationFixture.CURRENT_PASSWORD);
        var testUserDetails = new UserDetailsImpl(testUser);

        when(authenticatedUserUtilService.getLoggedInUser())
                .thenReturn(testUserDetails);
        when(userJpaRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.currentPassword(), testUser.getPassword()))
                .thenReturn(true);

        assertEquals(request.currentPassword(), testUser.getPassword());
        assertNotEquals(request.newPassword(), request.confirmationPassword());
        assertThrows(
                ConfirmationPasswordException.class,
                () -> changePasswordService.changePassword(request));

        verify(authenticatedUserUtilService).getLoggedInUser();
        verify(userJpaRepository, times(2)).findByEmail(testUser.getEmail());
        verify(passwordEncoder).matches(request.currentPassword(), testUser.getPassword());
        verify(userJpaRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_shouldThrowExceptionWhenNewPasswordMatchesCurrentPassword() {
        var request = AuthenticationFixture.createSamePasswordRequest();
        var testUser = UserFixture.createTestUser();
        testUser.setPassword(AuthenticationFixture.CURRENT_PASSWORD);
        var testUserDetails = new UserDetailsImpl(testUser);

        when(authenticatedUserUtilService.getLoggedInUser())
                .thenReturn(testUserDetails);
        when(userJpaRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.currentPassword(), testUser.getPassword()))
                .thenReturn(true);

        assertEquals(request.currentPassword(), testUser.getPassword());
        assertEquals(request.currentPassword(), request.newPassword());
        assertThrows(
                SamePasswordException.class,
                () -> changePasswordService.changePassword(request));

        verify(authenticatedUserUtilService).getLoggedInUser();
        verify(userJpaRepository, times(2)).findByEmail(testUser.getEmail());
        verify(passwordEncoder).matches(request.currentPassword(), testUser.getPassword());
        verify(userJpaRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_shouldThrowExceptionWhenUserNotFound() {
        var request = AuthenticationFixture.createValidChangePasswordRequest();
        var testUser = UserFixture.createTestUser();
        var testUserDetails = new UserDetailsImpl(testUser);

        when(authenticatedUserUtilService.getLoggedInUser())
                .thenReturn(testUserDetails);
        when(userJpaRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(
                UnauthorizedAccessException.class,
                () -> changePasswordService.changePassword(request));

        verify(authenticatedUserUtilService).getLoggedInUser();
        verify(userJpaRepository).findByEmail(testUser.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userJpaRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_shouldThrowExceptionWhenUserNotAuthenticated() {
        var request = AuthenticationFixture.createValidChangePasswordRequest();

        when(authenticatedUserUtilService.getLoggedInUser())
                .thenThrow(new UnauthorizedAccessException("User is not logged in"));

        assertThrows(
                UnauthorizedAccessException.class,
                () -> changePasswordService.changePassword(request));

        verify(authenticatedUserUtilService).getLoggedInUser();
        verify(userJpaRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userJpaRepository, never()).save(any(User.class));
    }
}