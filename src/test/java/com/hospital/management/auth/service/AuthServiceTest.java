package com.hospital.management.auth.service;

import com.hospital.management.auth.dto.LoginRequest;
import com.hospital.management.auth.dto.LoginResponse;
import com.hospital.management.config.JwtProperties;
import com.hospital.management.user.model.Role;
import com.hospital.management.user.model.UserDocument;
import com.hospital.management.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        JwtService jwtService = new JwtService(new JwtProperties(
                "test-secret-key-that-is-long-enough-for-hs256-signing",
                3_600_000L,
                "hospital-management-test"
        ));
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void loginReturnsBearerTokenForValidCredentials() {
        UserDocument user = enabledUser();
        when(userRepository.findByEmailIgnoreCase("admin@hospital.local")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("ChangeMe123!", "hashed")).thenReturn(true);

        LoginResponse response = authService.login(new LoginRequest("admin@hospital.local", "ChangeMe123!"));

        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.email()).isEqualTo("admin@hospital.local");
        assertThat(response.roles()).containsExactly(Role.ADMIN);
        assertThat(response.expiresInMs()).isEqualTo(3_600_000L);
    }

    @Test
    void loginRejectsUnknownUser() {
        when(userRepository.findByEmailIgnoreCase("missing@hospital.local")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("missing@hospital.local", "Password1")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void loginRejectsDisabledAccount() {
        UserDocument user = enabledUser();
        user.setEnabled(false);
        when(userRepository.findByEmailIgnoreCase("admin@hospital.local")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(new LoginRequest("admin@hospital.local", "ChangeMe123!")))
                .isInstanceOf(DisabledException.class);
    }

    @Test
    void loginRejectsInvalidPassword() {
        UserDocument user = enabledUser();
        when(userRepository.findByEmailIgnoreCase("admin@hospital.local")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("admin@hospital.local", "wrong")))
                .isInstanceOf(BadCredentialsException.class);
    }

    private UserDocument enabledUser() {
        UserDocument user = new UserDocument();
        user.setId("admin-1");
        user.setEmail("admin@hospital.local");
        user.setPasswordHash("hashed");
        user.setRoles(Set.of(Role.ADMIN));
        user.setEnabled(true);
        return user;
    }
}
