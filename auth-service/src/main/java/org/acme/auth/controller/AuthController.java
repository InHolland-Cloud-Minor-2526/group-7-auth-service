package org.acme.auth.controller;

import org.acme.auth.dto.LoginRequest;
import org.acme.auth.dto.LoginResponse;
import org.acme.auth.dto.RefreshTokenRequestDTO;
import org.acme.auth.dto.RefreshTokenResponseDTO;
import org.acme.auth.dto.RegistrationDTO;
import org.acme.auth.services.AuthService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Request body is required")
                    .build();
        }
        LoginResponse loginResponse = authService.generateTokens(request);
        return Response.status(Response.Status.OK)
                .entity(loginResponse)
                .build();
    }

    @POST
    @Path("/registration")
    public Response registerUser(@Valid RegistrationDTO registrationDTO) {
        if (registrationDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Request body is required")
                    .build();
        }
        return Response.status(Response.Status.CREATED)
                .entity(authService.registerUser(registrationDTO))
                .build();

    }

    @POST
    @Path("/refreshtoken")
    public Response getNewAccessTokenWithRefreshToken(@Valid RefreshTokenRequestDTO refreshTokenDTO) {
        if (refreshTokenDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Request body is required")
                    .build();
        }
        RefreshTokenResponseDTO responseDTO = new RefreshTokenResponseDTO();
        responseDTO.accessToken = authService.getNewAccessTokenWithRefreshToken(refreshTokenDTO.refreshToken);
        return Response.status(Response.Status.OK)
                .entity(responseDTO)
                .build();

    }

}
