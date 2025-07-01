package server.api;

import commons.Debt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.service.DebtService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

public class DebtControllerTestIndirectOutputTest {

    @Mock
    private DebtService debtService;

    @InjectMocks
    private DebtController debtController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        debtController = new DebtController(debtService);
    }

    @Test
    void getParticipantsDebtCallsDebtService() {
        debtController.getParticipantsDebt(1L, 2L);
        verify(debtService).getDebtByParticipantID(anyLong());
    }
    @Test
    void getEventDebtCallsDebtService() {
        debtController.getEventDebt(1L);
        verify(debtService).getDebtByEventID(anyLong());
    }
    @Test
    void updatePaidCallsDebtService() {
        Long debtId = 1L;
        Boolean paid = true;
        debtController.updatePaid(debtId, paid);
        verify(debtService).updateDebt(eq(debtId), isNull(), eq(paid));
    }
    @Test
    void addDebtCallsDebtService() {
        Debt newDebt = new Debt();
        debtController.addDebt(newDebt);
        verify(debtService).addDebt(eq(newDebt));
    }
}

