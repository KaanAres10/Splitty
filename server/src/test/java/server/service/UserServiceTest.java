package server.service;

import commons.Event;
import commons.Participant;
import commons.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.EventRepository;
import server.database.ParticipantRepository;
import server.database.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith (MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void saveUserTest() {
        User user = new User();
        user.setUsername("name");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.saveUser(user);
        assertNotNull(savedUser);
        assertEquals("name", savedUser.getUsername());
        assertEquals("en", savedUser.getPreferredLanguage());
    }

    @Test
    public void createUser() {
        User user = mock(User.class);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser();
        assertNotNull(result);
        assertEquals(result, user);
    }

    @Test
    public void getByIdTest() {
        User user = mock(User.class);
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getById(1L);
        assertNotNull(result);
        assertEquals(result, user);
    }

    @Test
    public void getEventsByUserTest() {
        User user = mock(User.class);
        user.setId(1L);
        Event event = new Event();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getById(1L);
        assertNotNull(result);
        assertEquals(result, user);
    }

    @Test
    public void testGetEventsByUser() {
        User user = new User();
        user.setId(1L);

        Event event1 = new Event();
        event1.setId(100L);
        event1.addParticipant(new Participant(user, event1));

        Event event2 = new Event();
        event2.setId(101L);
        event2.addParticipant(new Participant(user, event2));

        when(eventRepository.findByParticipantsContains(1L)).thenReturn(Arrays.asList(event1, event2));

        List<Event> events = userService.getEventsByUser(1L);
        assertEquals(2, events.size());
        assertEquals(event1, events.get(0));
        assertEquals(event2, events.get(1));
    }
}
