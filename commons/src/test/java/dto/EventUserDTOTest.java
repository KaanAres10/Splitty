package dto;

import commons.Event;
import commons.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class EventUserDTOTest {
    private Event event;
    private User user;
    private EventUserDTO eventUserDTO;

    @BeforeEach
    public void setUp() {
        event = new Event(); // Assuming Event has a default constructor
        user = new User();   // Assuming User has a default constructor
        eventUserDTO = new EventUserDTO();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(eventUserDTO, "EventUserDTO should be initialized but it is null");
    }

    @Test
    public void testParameterizedConstructor() {
        eventUserDTO = new EventUserDTO(event, user);
        assertSame(event, eventUserDTO.getEvent(), "Constructor event does not match");
        assertSame(user, eventUserDTO.getUser(), "Constructor user does not match");
    }

    @Test
    public void testGetEvent() {
        eventUserDTO.setEvent(event);
        assertSame(event, eventUserDTO.getEvent(), "Getter for event does not return what was set");
    }

    @Test
    public void testGetUser() {
        eventUserDTO.setUser(user);
        assertSame(user, eventUserDTO.getUser(), "Getter for user does not return what was set");
    }

    @Test
    public void testSetEvent() {
        eventUserDTO.setEvent(event);
        assertSame(event, eventUserDTO.getEvent(), "Event not set correctly");
    }

    @Test
    public void testSetUser() {
        eventUserDTO.setUser(user);
        assertSame(user, eventUserDTO.getUser(), "User not set correctly");
    }
}
