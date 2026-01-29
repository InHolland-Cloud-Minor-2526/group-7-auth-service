package org.acme.auth.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.acme.auth.dto.LoginRequest;
import org.acme.auth.dto.LoginResponse;
import org.acme.auth.dto.ForgotPasswordRequest;
import org.acme.auth.dto.ForgotPasswordResponse;
import org.acme.auth.dto.ResetPasswordRequest;
import org.acme.auth.dto.ResetPasswordResponse;
import org.acme.auth.security.JwtUtil;
import org.acme.auth.services.AuthService;
import org.acme.auth.services.PasswordResetService;
import org.acme.auth.services.EmailService;

import org.acme.entity.User;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    JwtUtil jwtUtil;

    @Inject
    AuthService authService;

    @Inject
    PasswordResetService passwordResetService;

    @Inject
    EmailService emailService;

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

        String token = jwtUtil.generateToken(request.email);
        return Response.ok(new LoginResponse(token)).build();
    }

    @POST
    @Path("/forgot-password")
        public Response forgotPassword(ForgotPasswordRequest request) {
            if (request == null || request.email == null || request.email.isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("email is required")
                    .build();
            }

        // Always generic to prevent user enumeration
        String message = "If the account exists, a password reset message has been sent.";

        var issued = passwordResetService.issueResetTokenByEmail(request.email);

        // If user exists -> send via email (or log in dev)
        if (issued != null) {
            emailService.sendPasswordReset(issued.email, issued.rawToken);
        }

        // Never return token in response
        return Response.ok(new ForgotPasswordResponse(message, null)).build();
    }


    @POST
    @Path("/reset-password")
    public Response resetPassword(ResetPasswordRequest request) {

        if (request == null ||
                request.token == null || request.token.isBlank() ||
                request.newPassword == null || request.newPassword.isBlank()) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("token and newPassword are required")
                    .build();
        }

        if (request.newPassword.length() < 6) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Password must be at least 6 characters")
                    .build();
        }

        // Validate token and mark it used
        String email = passwordResetService.consumeValidTokenAndGetEmail(request.token);
        if (email == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid or expired token")
                    .build();
        }

        // Update password in DB
        authService.updatePassword(email, request.newPassword);

        return Response.ok(
            new ResetPasswordResponse("Password reset successfully")
        ).build();
    }
}
