package server.api;

import commons.Debt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.service.DebtService;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DebtControllerTest {

    @Mock
    private DebtService debtService;

    @InjectMocks
    private DebtController debtController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getParticipantsDebtValidInputReturnsDebts() {
        List<Debt> debts = new ArrayList<>();
        when(debtService.getDebtByParticipantID(anyLong())).thenReturn(debts);

        ResponseEntity<List<Debt>> response = debtController.getParticipantsDebt(1L, 2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(debts, response.getBody());
    }

    @Test
    void getParticipantsDebtNullInputReturnsBadRequest() {
        ResponseEntity<List<Debt>> response = debtController.getParticipantsDebt(null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getEventDebtValidInputReturnsDebts() {
        List<Debt> debts = new ArrayList<>();
        when(debtService.getDebtByEventID(anyLong())).thenReturn(debts);

        ResponseEntity<List<Debt>> response = debtController.getEventDebt(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(debts, response.getBody());
    }

    @Test
    void getEventDebtNullInputReturnsBadRequest() {
        ResponseEntity<List<Debt>> response = debtController.getEventDebt(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updatePaidNullInputReturnsBadRequest() {
        ResponseEntity<Debt> response = debtController.updatePaid(null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getDebtsByExpenseValidInputReturnsDebts() {
        List<Debt> debts = new ArrayList<>();
        when(debtService.getDebtByExpenseId(anyLong())).thenReturn(debts);

        ResponseEntity<List<Debt>> response = debtController.getDebtsByExpense(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(debts, response.getBody());
    }

    @Test
    void getDebtsByExpenseNullInputReturnsBadRequest() {
        ResponseEntity<List<Debt>> response = debtController.getDebtsByExpense(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    void addDebtNullInputReturnsBadRequest() {
        ResponseEntity<Debt> response = debtController.addDebt(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updatePaidWebSocketValidInputReturnsNull() {
        Long debtId = 1L;
        Boolean paid = true;

        Debt result = debtController.updatePaidWebSocket(debtId, paid);

        // The method is not implemented, so it should return null
        assertEquals(null, result);
    }


    @Test
    void updatePaidValidInputReturnsUpdatedDebt() {
        Long debtId = 1L;
        Boolean paid = true;
        Debt updatedDebt = new Debt();

        // Mocking successful debt update
        when(debtService.updateDebt(eq(debtId), isNull(), eq(paid))).thenReturn(updatedDebt);

        ResponseEntity<Debt> response = debtController.updatePaid(debtId, paid);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDebt, response.getBody());
    }

    @Test
    void updatePaidNullDebtIdReturnsBadRequest() {
        Long debtId = null;
        Boolean paid = true;

        ResponseEntity<Debt> response = debtController.updatePaid(debtId, paid);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // Verify that debtService.updateDebt() is not called
        verify(debtService, never()).updateDebt(anyLong(), any(), anyBoolean());
    }

    @Test
    void updatePaidNullPaidReturnsBadRequest() {
        Long debtId = 1L;
        Boolean paid = null;

        ResponseEntity<Debt> response = debtController.updatePaid(debtId, paid);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // Verify that debtService.updateDebt() is not called
        verify(debtService, never()).updateDebt(anyLong(), any(), anyBoolean());
    }

    @Test
    void updatePaidRuntimeExceptionReturnsBadRequest() {
        Long debtId = 1L;
        Boolean paid = true;

        // Mocking debt update operation throwing a RuntimeException
        when(debtService.updateDebt(eq(debtId), isNull(), eq(paid))).thenThrow(new RuntimeException());

        ResponseEntity<Debt> response = debtController.updatePaid(debtId, paid);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void addDebtValidInputReturnsAddedDebt() {
        Debt newDebt = new Debt();
        Debt addedDebt = new Debt();

        // Mocking successful debt addition
        when(debtService.addDebt(eq(newDebt))).thenReturn(addedDebt);

        ResponseEntity<Debt> response = debtController.addDebt(newDebt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(addedDebt, response.getBody());
    }

    @Test
    void addDebtNullDebtReturnsBadRequest() {
        ResponseEntity<Debt> response = debtController.addDebt(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // Verify that debtService.addDebt() is not called
        verify(debtService, never()).addDebt(any());
    }

    @Test
    void addDebtRuntimeExceptionReturnsBadRequest() {
        Debt newDebt = new Debt();

        // Mocking debt addition operation throwing a RuntimeException
        when(debtService.addDebt(eq(newDebt))).thenThrow(new RuntimeException());

        ResponseEntity<Debt> response = debtController.addDebt(newDebt);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

