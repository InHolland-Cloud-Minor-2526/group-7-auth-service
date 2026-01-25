package org.acme.auth.controller;

import org.acme.auth.dto.LoginRequest;
import org.acme.auth.dto.RefreshTokenRequestDTO;
import org.acme.auth.dto.RegistrationDTO;
import org.acme.auth.dto.RegistrationResponseDTO;
import org.acme.auth.entity.User;
import org.acme.auth.services.AuthService;
import org.acme.auth.utils.JwtUtil;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

@QuarkusTest
class AuthControllerTest {

    @InjectMock
    AuthService authService;

    @InjectMock
    JwtUtil jwtUtil;


    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.email = "test@mail.com";
        request.password = "123456";

        User user = new User();
        user.userId = 1L;

        Mockito.when(authService.validateUser(Mockito.any()))
               .thenReturn(user);

        Mockito.when(jwtUtil.generateAccessToken(1L))
               .thenReturn("access-token");

        Mockito.when(jwtUtil.generateRefreshToken(1L))
               .thenReturn("refresh-token");

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(200)
            .body("accessToken", notNullValue())
            .body("refreshToken", notNullValue());
    }

    @Test
    void login_nullBody_returns400() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(400);
    }

    @Test
    void registration_success() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.email = "new@mail.com";
        dto.password = "123456";

        Mockito.when(authService.registerUser(Mockito.any()))
               .thenReturn(new RegistrationResponseDTO("a", "r", 10L));

        given()
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .post("/auth/registration")
        .then()
            .statusCode(201);
    }


    @Test
    void refreshToken_success() {
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
        dto.refreshToken = "old-refresh-token";

        Mockito.when(authService.getNewAccessTokenWithRefreshToken(dto.refreshToken))
               .thenReturn("new-access-token");

        given()
            .contentType(ContentType.JSON)
            .body(dto)
        .when()
            .post("/auth/refreshtoken")
        .then()
            .statusCode(200)
            .body("accessToken", notNullValue());
    }
}
