package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.*;
import dto.EventUserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import server.service.EventService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = standaloneSetup(eventController).build();
    }

    @Test
    public void createEventTest() throws Exception {
        Event event = new Event();
        event.setTitle("Test Event");
        event.setId(1L);

        User user = new User();
        user.setUsername("testUser");

        EventUserDTO nullEventDTO = new EventUserDTO(null, user);
        ResponseEntity<Event> nullEventResponseEntity = eventController.createEvent(nullEventDTO);
        assertEquals(HttpStatus.BAD_REQUEST, nullEventResponseEntity.getStatusCode());

        EventUserDTO eventUserDTO = new EventUserDTO(event, user);

        when(eventService.createEvent(any(Event.class), any(User.class))).thenReturn(event);

        mockMvc.perform(post("/api/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Event"))
                .andExpect(jsonPath("$.id").value(1L));

        mockMvc.perform(post("/api/events/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(null)))
            .andExpect(status().isBadRequest());

    }
    @Test
    void changeEventTitleTest() {
        Long validEventId = 1L;
        String validNewTitle = "New Title";
        Event mockEvent = new Event("Old Title");
        Event changedEvent = new Event();
        changedEvent.setTitle(validNewTitle);
        changedEvent.setId(validEventId);
        when(eventService.changeTitle(validEventId, validNewTitle)).thenReturn(mockEvent);
        ResponseEntity<Event> responseEntity = eventController.changeEventTitle(validEventId, changedEvent);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockEvent, responseEntity.getBody());

        Long nullEventId = null;
        ResponseEntity<Event> nullEventIdResponseEntity = eventController.changeEventTitle(nullEventId, changedEvent);
        assertEquals(HttpStatus.BAD_REQUEST, nullEventIdResponseEntity.getStatusCode());

        Event empty = new Event();
        ResponseEntity<Event> nullNewTitleResponseEntity = eventController.changeEventTitle(validEventId, empty);
        assertEquals(HttpStatus.BAD_REQUEST, nullNewTitleResponseEntity.getStatusCode());

        empty.setTitle("");
        ResponseEntity<Event> emptyNewTitleResponseEntity = eventController.changeEventTitle(validEventId, empty);
        assertEquals(HttpStatus.BAD_REQUEST, emptyNewTitleResponseEntity.getStatusCode());

        Long nonExistentEventId = 2L;
        when(eventService.changeTitle(nonExistentEventId, validNewTitle)).thenReturn(null);
        ResponseEntity<Event> notFoundResponseEntity = eventController.changeEventTitle(nonExistentEventId, changedEvent);
        assertEquals(HttpStatus.BAD_REQUEST, notFoundResponseEntity.getStatusCode());

        verify(eventService, times(1)).changeTitle(validEventId, validNewTitle);
        verify(eventService, times(1)).changeTitle(nonExistentEventId, validNewTitle);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void deleteEvent() throws Exception {
        Long eventId = 1L;

        when(eventService.deleteEvent(eventId)).thenReturn(true);
        // Perform the Delete request
        mockMvc.perform(delete("/api/events/{eventId}/delete", eventId))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/events/{eventId}/delete", 2L))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteNullIdEvent() throws Exception {
        Long eventId = null;

        mockMvc.perform(delete("/api/events/{eventId}/delete",eventId))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateCodeTest() throws Exception {
        long eventId = 1L;
        Event event = new Event();
        event.setTitle("event");
        event.setId(1L);

        when(eventService.findById(eventId)).thenReturn(event);
        when(eventService.saveEvent(any(Event.class))).thenReturn(event);

        mockMvc.perform(post("/api/events/{eventId}/updateCode",eventId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("event"))
            .andExpect(jsonPath("$.inviteCode").exists());

    }

    @Test
    public void updateCodeNullIdTest() throws Exception {
        long eventId = 1L;
        Event event = new Event();
        event.setTitle("event");
        event.setId(1L);

        mockMvc.perform(post("/api/events/{eventId}/updateCode",eventId))
            .andExpect(status().isNotFound());
    }

    @Test
    public void getEventByIdTest() throws Exception {
        Event event = new Event();
        long eventId = 1L;
        event.setId(eventId);
        event.setTitle("test");

        when(eventService.findById(eventId)).thenReturn(event);

        mockMvc.perform(get("/api/events/{eventId}",eventId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("test"));

        mockMvc.perform(get("/api/events/{eventId}",2L))
            .andExpect(status().isNotFound());
    }
    @Test
    void getByInviteCodeTest() {
        String validInviteCode = "validInviteCode";
        Event mockEvent = new Event("Test Event");
        when(eventService.findByInviteCode(validInviteCode)).thenReturn(mockEvent);
        ResponseEntity<Event> responseEntity = eventController.getEventByInviteCode(validInviteCode);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockEvent, responseEntity.getBody());

        String nullInviteCode = null;
        ResponseEntity<Event> nullResponseEntity = eventController.getEventByInviteCode(nullInviteCode);
        assertEquals(HttpStatus.BAD_REQUEST, nullResponseEntity.getStatusCode());

        String emptyInviteCode = "";
        ResponseEntity<Event> emptyResponseEntity = eventController.getEventByInviteCode(emptyInviteCode);
        assertEquals(HttpStatus.BAD_REQUEST, emptyResponseEntity.getStatusCode());

        String nonExistentInviteCode = "nonExistentInviteCode";
        when(eventService.findByInviteCode(nonExistentInviteCode)).thenReturn(null);
        ResponseEntity<Event> notFoundResponseEntity = eventController.getEventByInviteCode(nonExistentInviteCode);
        assertEquals(HttpStatus.NOT_FOUND, notFoundResponseEntity.getStatusCode());

        verify(eventService, times(1)).findByInviteCode(validInviteCode);
        verify(eventService, times(1)).findByInviteCode(nonExistentInviteCode);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    public void getAllTest() throws Exception {
        List<Event> list = new ArrayList<>();

        when(eventService.getAllEvents()).thenReturn(list);

        mockMvc.perform(get("/api/events/all"))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllTransfersTest() throws Exception {
        Event event = new Event();
        long eventId = 1L;
        event.setId(eventId);
        List<Transfer> list = new ArrayList<>();

        when(eventService.findById(eventId)).thenReturn(event);
        when(eventService.getAllTransfersByEventId(eventId)).thenReturn(list);

        mockMvc.perform(get("/api/events/{eventId}/transfers/all",eventId))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/events/{eventId}/transfers/all",2L))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllDebtsTest() throws Exception {
        Event event = new Event();
        long eventId = 1L;
        event.setId(eventId);
        List<Debt> list = new ArrayList<>();

        when(eventService.findById(eventId)).thenReturn(event);
        when(eventService.getAllDebtsByEventId(eventId)).thenReturn(list);

        mockMvc.perform(get("/api/events/{eventId}/debts/all",eventId))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/events/{eventId}/debts/all",2L))
            .andExpect(status().isBadRequest());
    }

}
