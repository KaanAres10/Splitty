package server.service;

import commons.Event;
import commons.Participant;
import commons.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.EventLastActivityListener;
import server.database.EventRepository;
import server.database.ParticipantRepository;
import server.database.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith (MockitoExtension.class)
public class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventLastActivityListener eventLastActivityListener;

    @InjectMocks
    private ParticipantService participantService;

    @Test
    public void addParticipantToEventTest() {
        Long userId = 1L;
        Long eventId = 1L;
        User user = mock(User.class);
        Event event = mock(Event.class);
        Participant participant = new Participant(user, event);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        Participant result = participantService.addParticipantToEvent(userId, eventId);

        assertNotNull(result);
    }

    @Test
    public void deleteParticipant() {
        Long participantId = 1L;
        User user = mock(User.class);
        Event event = mock(Event.class);
        Participant participant = new Participant(user, event);

        when(participantRepository.findById(participantId)).thenReturn(Optional.of(participant));
        doNothing().when(participantRepository).deleteById(participantId);
        assertTrue(participantService.deleteParticipant(participantId));
    }

    @Test
    public void testAddParticipantToEventWithUserIdAndEventId() {
        // Mocking
        User user = new User();
        user.setId(1L);
        Event event = new Event();
        event.setId(2L);
        Participant p = new Participant();
        doNothing().when(eventLastActivityListener).postPersist(any(Object.class));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(2L)).thenReturn(Optional.of(event));
        when(participantRepository.save(any(Participant.class))).thenReturn(p);
        
        // Test
        Participant participant = participantService.addParticipantToEvent(1L, 2L);
        assertNotNull(p);
        assertEquals(p, participant);
    }

    @Test
    public void testAddParticipantToEventWithParticipantObject() {
        // Mocking
        User user = new User();
        user.setId(1L);
        Event event = new Event();
        event.setId(2L);
        Participant participant = new Participant();
        participant.setEvent(event);
        participant.setUser(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        // Test
        Participant result = participantService.addParticipantToEvent(participant);
        assertNotNull(result);
    }

    @Test
    public void testUpdateParticipant() {
        // Mocking
        Participant existingParticipant = new Participant();
        existingParticipant.setId(1L);
        when(participantRepository.findById(1L)).thenReturn(Optional.of(existingParticipant));
        when(participantRepository.save(any(Participant.class))).thenReturn(existingParticipant);

        // Test
        Participant participant = new Participant();
        participant.setId(1L);
        Participant result = participantService.updateParticipant(participant);
        assertNotNull(result);
    }

    @Test
    public void testGetAllParticipantsByEventId() {
        // Mocking
        List<Participant> participants = new ArrayList<>();
        when(participantRepository.findAllByEventId(anyLong())).thenReturn(participants);

        // Test
        List<Participant> result = participantService.getAllParticipantsByEventId(1L);
        assertEquals(participants, result);
    }

    @Test
    public void testDeleteParticipant() {

        Participant p = new Participant();
        p.setId(1L);
        // Mocking
        when(participantRepository.findById(1L)).thenReturn(Optional.of(new Participant()));
        when(participantRepository.findById(2L)).thenReturn(Optional.empty());


        // Test
        boolean result = participantService.deleteParticipant(1L);
        assertTrue(result);
        boolean result2 = participantService.deleteParticipant(2L);
        assertFalse(result2);
    }

    @Test
    public void testDeleteParticipantByUserId() {
        User user = new User();
        user.setId(1L);
        Event event = new Event();
        event.setId(2L);
        Participant participant = new Participant();
        participant.setEvent(event);
        participant.setUser(user);
        List<Participant> participants = new ArrayList<>();
        participant.setId(1L);
        participants.add(participant);
        when(participantRepository.findAllByEventId(2L)).thenReturn(participants);
        when(participantRepository.findAllByEventId(1L)).thenReturn(new ArrayList<>());

        // Test
        boolean result = participantService.deleteParticipantByUserId(2L, 1L);
        assertTrue(result);
        boolean result2 = participantService.deleteParticipantByUserId(1L, 1L);
        assertFalse(result2);
    }

    @Test
    public void testGetParticipantByName() {
        // Mocking
        List<Participant> participants = new ArrayList<>();
        Event event = new Event();
        event.setId(1L);
        Participant participant = new Participant();
        participant.setEvent(event);
        participant.setName("John");
        participants.add(participant);
        when(participantRepository.findAllByEventId(1L)).thenReturn(participants);
        when(participantRepository.findAllByEventId(2L)).thenReturn(new ArrayList<>());

        Participant result = participantService.getParticipantByName(1L, "John");
        assertNotNull(result);
        assertEquals(participant, result);

        Participant result2 = participantService.getParticipantByName(2L, "John");
        assertNull(result2);
    }
}
