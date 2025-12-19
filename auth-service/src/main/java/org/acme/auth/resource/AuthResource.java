package org.acme.auth.resource;

import org.acme.auth.dto.LoginRequest;
import org.acme.auth.dto.LoginResponse;
import org.acme.auth.dto.RegistrationDTO;
import org.acme.auth.dto.RegistrationResponseDTO;
import org.acme.auth.security.JwtUtil;
import org.acme.auth.services.AuthService;
import org.acme.entity.User;
import org.acme.auth.security.JwtUtil;
import org.acme.auth.services.AuthService;
import org.acme.customExceptions.CustomAuthException;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
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
        Long userId;
        String token;

        try {
            userId = authService.validateUser(request);
            token = jwtUtil.generateToken(userId);
            authService.updateToken(userId, token);

        } catch (CustomAuthException e) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();
        }

        return Response.ok(
                new LoginResponse(token, userId)).build();
    }

    @POST
    @Path("/registration")
    public Response registration(RegistrationDTO registrationDTO) {
        try {
            User user = authService.createUser(registrationDTO);
            String token = jwtUtil.generateToken(user.email);

            return Response.status(Response.Status.CREATED)
                    .entity(new RegistrationResponseDTO(token))
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(java.util.Map.of("error", e.getMessage()))
                    .build();
        }
    }

}
