package org.acme.clients;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@RegisterRestClient(configKey = "user-service")
public interface UserServiceClient {

    @POST
    @Path("/users/{userId}")
    Response createDefaultProfile(@PathParam("userId") Long userId);
}
