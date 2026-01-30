package org.acme.auth.customExceptions;

public class NewAccessTokenGeneration extends AuthException {

    public NewAccessTokenGeneration() {
        super("New access token generation failed");
    }
}
