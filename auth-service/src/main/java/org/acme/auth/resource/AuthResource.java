package org.acme.auth.resource;

import org.acme.auth.dto.LoginRequest;
import org.acme.auth.dto.LoginResponse;
import org.acme.auth.dto.RegistrationDTO;
import org.acme.auth.dto.RegistrationResponseDTO;
import org.acme.auth.security.JwtUtil;
import org.acme.auth.services.AuthService;
import org.acme.customExceptions.CustomAuthException;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    JwtUtil jwtUtil;

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {

            Long userId = authService.validateUser(request);
            String accessToken = jwtUtil.generateAccessToken(userId);
            String refreshToken = jwtUtil.generateRefreshToken(userId);
            authService.updateToken(userId, accessToken, refreshToken);

            return Response.ok(
                    new LoginResponse(accessToken, refreshToken)).build();

        } catch (CustomAuthException e) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();
        }

    }

    @POST
    @Path("/registration")
    public Response registration(RegistrationDTO registrationDTO) {

        RegistrationResponseDTO tokens = authService.createUser(registrationDTO);

        return Response.status(Response.Status.CREATED)
                .entity(tokens)
                .build();
    }

    @GET
    @Path("/hi")
    public Response hi() {

        return Response.status(Response.Status.OK)
                .build();
    }

}
