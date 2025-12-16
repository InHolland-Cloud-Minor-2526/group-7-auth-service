package org.acme.auth.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.acme.auth.dto.LoginRequest;
import org.acme.auth.dto.LoginResponse;
import org.acme.auth.security.JwtUtil;
import org.acme.auth.service.AuthService;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @Inject
    JwtUtil jwtUtil;

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {

        // Validate input
        if (request.email == null || request.password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Email and password are required")
                    .build();
        }

        // Authenticate user using email
        boolean authenticated = authService.authenticate(
                request.email,
                request.password
        );

        // Send the notification if it's fail
        if (!authenticated) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid email or password")
                    .build();
        }

        // Generate JWT token using email as identity
        String token = jwtUtil.generateToken(request.email);

        // Return token + email
        return Response.ok(
                new LoginResponse(token, request.email)
        ).build();
    }
}
