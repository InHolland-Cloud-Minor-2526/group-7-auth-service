package org.acme.auth.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EmailService {

    private static final Logger LOG = Logger.getLogger(EmailService.class);

    public void sendPasswordReset(String email, String link) {
        LOG.infof("PASSWORD RESET email=%s link=%s", email, link);
    }
}
