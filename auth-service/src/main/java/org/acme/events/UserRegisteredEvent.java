package org.acme.events;

public class UserRegisteredEvent {

    public Long userId;

    public UserRegisteredEvent() {
    }

    public UserRegisteredEvent(Long userId) {
        this.userId = userId;
    }
}
