package org.acme.auth.customExceptions;

public class UserExistsException extends AuthException {

    public UserExistsException() {
        super("User already exists");
    }
}
