package server.service;

import commons.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith (MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private TransferRepository transferRepository;
    @Mock
    private DebtRepository debtRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @InjectMocks
    private EventService eventService;

    @Test
    public void saveEventTest() {
        Event event = new Event();
        event.setTitle("Test Event");
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event savedEvent = eventService.saveEvent(event);
        assertNotNull(savedEvent);
        assertEquals("Test Event", savedEvent.getTitle());
    }

    @Test
    public void createEventTest() {
        Event event = new Event();
        event.setTitle("Test Event");
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event createdEvent = eventService.createEvent(event, new User());
        assertNotNull(createdEvent);
        assertEquals("Test Event", createdEvent.getTitle());
    }

    @Test
    public void changeTitleEventTest() {
        Event event = new Event();
        event.setTitle("Test event");
        String newTitle = "New Title";

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event changedEvent = eventService.changeTitle(event.getId(), newTitle);
        Event changedEvent2 = eventService.changeTitle(5L, newTitle);

        assertEquals(newTitle, changedEvent.getTitle());
        assertNull(changedEvent2);
    }

    @Test
    public void deleteEvent() {
        Event event = new Event();

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        doNothing().when(eventRepository).deleteById(event.getId());

        assertTrue(eventService.deleteEvent(event.getId()));
        assertFalse(eventService.deleteEvent(5L));

    }

    @Test
    public void getAllTransfersByEventIdTest(){
        Event event = new Event();
        event.setId(1L);
        List<Transfer> transfers = new ArrayList<>();

        when(transferRepository.findByEventId(event.getId())).thenReturn(transfers);

        List<Transfer> retrieved = eventService.getAllTransfersByEventId(event.getId());
        assertNotNull(retrieved);
    }

    @Test
    public void getAllDebtsByEventIdTest(){
        Event event = new Event();
        event.setId(1L);
        List<Debt> debts = new ArrayList<>();

        when(debtRepository.findByEventId(event.getId())).thenReturn(debts);

        List<Debt> retrieved = eventService.getAllDebtsByEventId(event.getId());
        assertNotNull(retrieved);
    }

    @Test
    public void getAllExpensesByEventIdTest(){
        Event event = new Event();
        event.setId(1L);
        List<Expense> expenses = new ArrayList<>();

        when(expenseRepository.findByEventId(event.getId())).thenReturn(expenses);

        List<Expense> retrieved = eventService.getAllExpensesByEventId(event.getId());
        assertNotNull(retrieved);
    }

    @Test
    public void getAllTagsByEventIdTest(){
        Event event = new Event();
        event.setId(1L);
        List<Tag> tags = new ArrayList<>();

        when(tagRepository.findTagsByEventId(event.getId())).thenReturn(tags);

        List<Tag> retrieved = eventService.getAllTagsByEventId(event.getId());
        assertNotNull(retrieved);
    }

    @Test
    public void findByIdTest(){
        Event event = new Event();
        event.setId(1L);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        Event retrieved = eventService.findById(event.getId());
        try {
            Event exception = eventService.findById(5L);
        }catch (RuntimeException e){
            assertEquals(e.getMessage(),"Event not found");
        }

        assertNotNull(retrieved);
        assertEquals(retrieved.getId(),event.getId());
    }

    @Test
    public void findByInviteCodeTest(){
        Event event = new Event();
        event.setInviteCode("welcome");

        when(eventRepository.findByInviteCode(event.getInviteCode())).thenReturn(Optional.of(event));

        Event retrieved = eventService.findByInviteCode("welcome");
        try {
            Event exception = eventService.findByInviteCode("not welcome");
        }catch (RuntimeException e){
            assertEquals(e.getMessage(),"Event not found");
        }

        assertNotNull(retrieved);
        assertEquals(retrieved.getId(),event.getId());
    }

    @Test
    public void getAllEventsTest(){
        List<Event> list = new ArrayList<>();

        when(eventRepository.getAllEvents()).thenReturn(list);

        List<Event> retrieved = eventService.getAllEvents();

        assertNotNull(retrieved);
        assertEquals(retrieved.size(),0);
    }
}