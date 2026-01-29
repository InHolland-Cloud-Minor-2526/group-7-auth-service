package org.acme.auth.dto;

public class ForgotPasswordResponse {

    public String message;

    // DEV ONLY – remove when email frontend is ready
    public String resetToken;

    public ForgotPasswordResponse(String message, String resetToken) {
        this.message = message;
        this.resetToken = resetToken;
    }
}
