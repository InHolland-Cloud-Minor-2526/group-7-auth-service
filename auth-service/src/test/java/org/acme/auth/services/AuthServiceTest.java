package org.acme.auth.services;

import org.acme.auth.customExceptions.InvalidCredential;
import org.acme.auth.customExceptions.UserExistsException;
import org.acme.auth.dto.LoginRequest;
import org.acme.auth.dto.RegistrationDTO;
import org.acme.auth.dto.RegistrationResponseDTO;
import org.acme.auth.entity.User;
import org.acme.auth.repository.AuthHandler;
import org.acme.messaging.UserEventPublisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class AuthServiceTest {

    @Inject
    AuthService authService;

    @InjectMock
    AuthHandler authHandler;

    @InjectMock
    UserEventPublisher userEventPublisher;

    @Test
    void validateUser_success() {
        LoginRequest request = new LoginRequest();
        request.email = "test@mail.com";
        request.password = "1234";

        User user = new User();
        user.userId = 1L;
        user.email = request.email;
        user.password = BcryptUtil.bcryptHash("1234");

        Mockito.when(authHandler.findUserByEmail(request.email))
                .thenReturn(user);

        User result = authService.validateUser(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Long.valueOf(1L), result.userId);
    }

    @Test
    void validateUser_invalidPassword() {
        LoginRequest request = new LoginRequest();
        request.email = "test@mail.com";
        request.password = "wrong";

        User user = new User();
        user.email = request.email;
        user.password = BcryptUtil.bcryptHash("1234");

        Mockito.when(authHandler.findUserByEmail(request.email))
                .thenReturn(user);

        InvalidCredential exception = Assertions.assertThrows(
                InvalidCredential.class,
                () -> authService.validateUser(request)
        );
        Assertions.assertNotNull(exception);

    }

    @Test
    void registerUser_userAlreadyExists() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.email = "exists@mail.com";

        Mockito.when(authHandler.findUserByEmail(dto.email))
                .thenReturn(new User());

        UserExistsException exception = Assertions.assertThrows(
                UserExistsException.class,
                () -> authService.registerUser(dto)
        );

        Assertions.assertNotNull(exception);

    }

    @Test
    void registerUser_success() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.email = "new@mail.com";
        dto.password = "1234";

        RegistrationResponseDTO response
                = new RegistrationResponseDTO("access", "refresh", 10L);

        Mockito.when(authHandler.findUserByEmail(dto.email))
                .thenReturn(null);

        Mockito.when(authHandler.registerUser(dto))
                .thenReturn(response);

        RegistrationResponseDTO result = authService.registerUser(dto);

        Assertions.assertEquals(Long.valueOf(10L), result.userId);
        Mockito.verify(userEventPublisher).publishUserRegistered(10L);
    }
}
