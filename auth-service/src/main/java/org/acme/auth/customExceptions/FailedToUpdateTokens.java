package org.acme.auth.customExceptions;

public class FailedToUpdateTokens extends AuthException {

    public FailedToUpdateTokens() {
        super("Failed to update tokens");
    }
}
