package org.acme.auth.services;

import io.quarkus.mailer.Mailer;
import io.quarkus.mailer.Mail;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EmailService {

    private static final Logger LOG = Logger.getLogger(EmailService.class);

    @Inject
    Mailer mailer;

    // Enable/disable real email sending
    @ConfigProperty(name = "app.mail.enabled", defaultValue = "false")
    boolean mailEnabled;

    // DEV ONLY: log token so you can test without email
    @ConfigProperty(name = "app.reset.dev-log-token", defaultValue = "true")
    boolean devLogToken;

    public void sendPasswordReset(String email, String rawToken) {

        // DEV ONLY logging (for Postman testing)
        if (devLogToken) {
            LOG.warnf(
                "DEV ONLY: Password reset token for %s: %s",
                email,
                rawToken
            );
        }

        // If email sending is disabled, stop here
        if (!mailEnabled) {
            return;
        }

        // Email content WITHOUT frontend
        String body =
                "You requested a password reset.\n\n"
              + "Use the following token to reset your password:\n\n"
              + rawToken + "\n\n"
              + "Call the API:\n"
              + "POST /auth/reset-password\n"
              + "Body:\n"
              + "{ \"token\": \"<token>\", \"newPassword\": \"<newPassword>\" }\n\n"
              + "This token expires shortly.\n"
              + "If you did not request this, you can ignore this email.\n";

        mailer.send(
            Mail.withText(
                email,
                "Password reset",
                body
            )
        );
    }
}
