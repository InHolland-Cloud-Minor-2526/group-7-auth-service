package org.acme.customExceptions;

public class CustomAuthException extends Exception {
    public CustomAuthException(String message) {
        super(message);
    }
}
