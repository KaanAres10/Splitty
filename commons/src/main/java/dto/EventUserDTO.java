package dto;

import commons.Event;
import commons.User;

public class EventUserDTO {
    private Event event;
    private User user;

    public EventUserDTO() {
    }

    public EventUserDTO(Event event, User user) {
        this.event = event;
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public User getUser() {
        return user;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
