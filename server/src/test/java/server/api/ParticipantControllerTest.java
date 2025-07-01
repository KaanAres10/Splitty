package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Event;
import commons.Participant;
import commons.User;
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
import server.database.ParticipantRepository;
import server.service.ParticipantService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith (MockitoExtension.class)
public class ParticipantControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private ParticipantService participantService;

    @InjectMocks
    private ParticipantController participantController;

    @BeforeEach
    public void setup() {
        mockMvc = standaloneSetup(participantController).build();
    }

    @Test
    public void addParticipantReturns200Test() throws Exception {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        User user = new User("TestUser", "en");
        user.setId(1L); // Ensure the user has an ID to pass the controller validation
        Participant participant = new Participant();
        participant.setUser(user);
        participant.setEvent(event);
        participant.setBic("bic");
        participant.setIban("iban");
        participant.setName("name");
        participant.setMail("asd");

        when(participantService.addParticipantToEvent(any(Participant.class))).thenReturn(
            participant);

            mockMvc.perform(post("/api/events/{eventId}/participants/addparticipant", eventId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(participant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.bic").value("bic"))
                .andExpect(jsonPath("$.iban").value("iban"))
                .andExpect(jsonPath("$.mail").value("asd"));
    }

    @Test
    public void addEmptyParticipantTest() throws Exception {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        User user = new User("TestUser", "en");
        user.setId(1L); // Ensure the user has an ID to pass the controller validation
        Participant participant = new Participant();
        participant.setUser(user);
        participant.setEvent(event);

        mockMvc.perform(post("/api/events/{eventId}/participants/addparticipant", eventId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(participant)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void editWrongParticipantTest() throws Exception {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        User user = new User("TestUser", "en");
        user.setId(1L); // Ensure the user has an ID to pass the controller validation
        Participant participant = new Participant();
        Long participantId =1L;
        participant.setUser(user);
        participant.setEvent(event);
        participant.setId(participantId);

        mockMvc.perform(put("/api/events/{eventId}/participants/{participantId}", eventId, participantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(participant)))
            .andExpect(status().isNotFound());

    }

    @Test
    public void updatesTest() throws Exception {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        User user = new User("TestUser", "en");
        user.setId(1L); // Ensure the user has an ID to pass the controller validation
        Participant participant = new Participant();
        Long participantId =1L;
        participant.setUser(user);
        participant.setEvent(event);
        participant.setId(participantId);

        mockMvc.perform(get("/api/events/{eventId}/participants/updates", eventId))
            .andExpect(status().isOk());

    }

    @Test
    public void deleteParticipantTest() throws Exception {
        Long participantId = 1L;
        Long eventId = 1L;
        when(participantService.deleteParticipant(participantId)).thenReturn(true);

            mockMvc.perform(
                    delete("/api/events/{eventId}/participants/{participantId}/delete", eventId,
                        participantId))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteNoIdParticipantTest() throws Exception {
        Long eventId = 1L;
        when(participantService.deleteParticipant(2L)).thenReturn(false);

        mockMvc.perform(
                delete("/api/events/{eventId}/participants/{participantId}/delete", eventId, 2L))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllParticipants() throws Exception {
        Long eventId = 1L;
        List<Participant> list = new ArrayList<>();

        when(participantService.getAllParticipantsByEventId(anyLong())).thenReturn(list);

            mockMvc.perform(get("/api/events/{eventId}/participants", eventId))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    public void getParticipantByName() throws Exception {
        Event e = new Event();
        e.setId(1L);
        Participant participant = new Participant();
        participant.setName("name");

        when(participantService.getParticipantByName(anyLong(), anyString())).thenReturn(
            participant);

            mockMvc.perform(get("/api/events/{eventId}/participants/{name}", 1L, "name"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void editParticipantTest() throws Exception {
        Participant p1 = new Participant();
        p1.setName("name");
        p1.setId(1L);

        Participant p2 = new Participant();
        p2.setName("updated");
        p2.setId(1L);

        when(participantService.updateParticipant(any(Participant.class))).thenReturn(p2);

            mockMvc.perform(
                    put("/api/events/{eventId}/participants/{participantId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(p2)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testAddParticipantBadRequest() {
        ResponseEntity<Participant> response = participantController.addParticipant(new Participant());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateParticipantSuccess() {
        Participant participant = new Participant();
        when(participantService.updateParticipant(any())).thenReturn(participant);
        ResponseEntity<Participant> response = participantController.updateParticipant(participant, 1L, 1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(participant, response.getBody());
    }

    @Test
    public void testUpdateParticipantNotFound() {
        when(participantService.updateParticipant(any())).thenReturn(null);
        ResponseEntity<Participant> response = participantController.updateParticipant(new Participant(), 1L, 1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetUpdatesSuccess() {
        // Test DeferredResult, should return NO_CONTENT
        assertNotNull(participantController.getUpdates());
    }

    @Test
    public void testGetAllParticipantsSuccess() {
        List<Participant> participants = new ArrayList<>();
        when(participantService.getAllParticipantsByEventId(anyLong())).thenReturn(participants);
        assertEquals(participants, participantController.getAllParticipants(1L));
    }

    @Test
    public void testDeleteParticipantSuccess() {
        when(participantService.deleteParticipant(anyLong())).thenReturn(true);
        ResponseEntity<String> response = participantController.deleteParticipant(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Participant was successfully deleted!", response.getBody());
    }

    @Test
    public void testDeleteParticipantBadRequest() {
        when(participantService.deleteParticipant(anyLong())).thenReturn(false);
        ResponseEntity<String> response = participantController.deleteParticipant(1L);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetByNameSuccess() {
        Participant participant = new Participant();
        when(participantService.getParticipantByName(anyLong(), anyString())).thenReturn(participant);
        assertEquals(participant, participantController.getByName("name", 1L));
    }
}

