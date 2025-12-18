package org.acme.auth.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.acme.auth.dto.LoginRequest;
import org.acme.auth.dto.LoginResponse;
import org.acme.auth.dto.UserDTO;
import org.acme.auth.security.JwtUtil;
import org.acme.auth.services.AuthService;

import org.acme.entity.User;

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
            User user = authService.validateUser(request);
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();
        }

        // Generate JWT token using email as identity
        String token = jwtUtil.generateToken(request.email);

        // Return token + email
        return Response.ok(
                new LoginResponse(token)).build();
    }
}
