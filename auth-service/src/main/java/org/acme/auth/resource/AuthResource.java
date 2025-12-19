package org.acme.auth.resource;

import org.acme.auth.dto.LoginRequest;
import org.acme.auth.dto.LoginResponse;
import org.acme.auth.dto.RefreshTokenRequest;
import org.acme.auth.security.JwtUtil;
import org.acme.auth.services.AuthService;
import org.acme.entity.User;

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
        User user;
        try {
            user = authService.validateUser(request);
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();
        }

        // Generate JWT token using email as identity
        String token = jwtUtil.generateToken(user.email);
        String refreshToken = authService.issueRefreshToken(user);

        // Return token + email
        return Response.ok(
                new LoginResponse(token, refreshToken)).build();
    }

    @POST
    @Path("/refresh")
    public Response refresh(RefreshTokenRequest request) {
        User user;
        try {
            user = authService.validateRefreshToken(request != null ? request.refreshToken : null);
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();
        }

        String token = jwtUtil.generateToken(user.email);
        String refreshToken = authService.issueRefreshToken(user);

        return Response.ok(new LoginResponse(token, refreshToken)).build();
    }
}
