package org.acme.auth.customExceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AuthExceptionMapper implements ExceptionMapper<AuthException> {

    @Override
    public Response toResponse(AuthException ex) {
        return Response.status(Response.Status.CONFLICT)
                .entity(ex.getMessage())
                .build();
    }
}
