package org.acme.auth.customExceptions;

public class RegistrationFailedException extends AuthException {

    public RegistrationFailedException() {
        super("User registration failed");
    }
}
