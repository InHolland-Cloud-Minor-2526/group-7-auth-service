package org.acme.auth.customExceptions;

public class InvalidCredential extends AuthException {

    public InvalidCredential() {
        super("Invalid credentials provided");
    }
}