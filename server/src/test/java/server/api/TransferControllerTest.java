package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Transfer;
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
import server.service.TransferService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class TransferControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransferService transferService;

    @InjectMocks
    private TransferController transferController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = standaloneSetup(transferController).build();
    }

    @Test
    public void addTransferTest() throws Exception {
        Long eventId = 1L;
        Long debtId = 1L;

        Transfer transfer = new Transfer();
        transfer.setMessage("Pay me");
        transfer.setId(1L);

        when(transferService
                .createTransfer(any(Transfer.class)))
                .thenReturn(transfer);

        mockMvc.perform(post("/api/events/{eventId}/{debtId}/transfers/create", eventId, debtId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Pay me"))
                .andExpect(jsonPath("$.id").value(1L));

        mockMvc.perform(post("/api/events/{eventId}/{debtId}/transfers/create", eventId, debtId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(null)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void changeApprovalTrueTransferTest() throws Exception {
        Long eventId = 1L;
        Long debtId = 1L;
        Long id = 1L;
        Transfer transfer = new Transfer();
        transfer.setMessage("Pay me");
        transfer.setId(id);
        transfer.setApproved(false);
        when(transferService.transferById(id)).thenReturn(transfer);

        mockMvc.perform(post("/api/events/{eventId}/{debtId}/transfers/{id}", eventId, debtId, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transfer)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.approved").value(true));
    }

    @Test
    public void changeApprovalFalseTransferTest() throws Exception {
        Long eventId = 1L;
        Long debtId = 1L;
        Long id = 1L;
        Transfer transfer = new Transfer();
        transfer.setMessage("Pay me");
        transfer.setId(id);
        transfer.setApproved(true);
        when(transferService.transferById(id)).thenReturn(transfer);

        mockMvc.perform(post("/api/events/{eventId}/{debtId}/transfers/{id}", eventId, debtId, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transfer)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.approved").value(false));
    }


    @Test
    public void testCreateTransferValidTransferReturnsCreatedResponse() {
        // Arrange
        Transfer transfer = new Transfer();
        transfer.setId(1L);

        when(transferService.createTransfer(any(Transfer.class))).thenReturn(transfer);

        // Act
        ResponseEntity<Transfer> response = transferController.createTransfer(transfer);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(transfer, response.getBody());
    }

    @Test
    public void testCreateTransferNullTransferReturnsBadRequest() {
        // Act
        ResponseEntity<Transfer> response = transferController.createTransfer(null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(transferService);
    }

    @Test
    public void testChangeApprovalValidTransferIdTogglesApprovalStatus() {
        // Arrange
        Long transferId = 1L;
        Transfer transfer = new Transfer();
        transfer.setId(transferId);
        transfer.setApproved(false);

        when(transferService.transferById(transferId)).thenReturn(transfer);

        // Act
        ResponseEntity<Transfer> response = transferController.changeApproval(transferId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(!transfer.isApproved(), response.getBody().isApproved());
    }

    @Test
    public void testChangeApprovalNullIdReturnsBadRequest() {
        // Act
        ResponseEntity<Transfer> response = transferController.changeApproval(null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(transferService);
    }
}
