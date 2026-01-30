package org.acme.messaging;

import org.acme.events.UserRegisteredEvent;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserEventPublisher {

    @Inject
    @Channel("user-registered-out")
    Emitter<UserRegisteredEvent> emitter;

    public void publishUserRegistered(Long userId) {

        System.out.println("Event" + userId );
        UserRegisteredEvent event = new UserRegisteredEvent(userId);
        emitter.send(event);
    }
}