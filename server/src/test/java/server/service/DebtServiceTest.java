package server.service;

import commons.Debt;
import commons.Event;
import commons.Expense;
import commons.Participant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.EventLastActivityListener;
import server.database.DebtRepository;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class DebtServiceTest {

    @Mock
    private DebtRepository debtRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private EventLastActivityListener eventLastActivityListener;

    @InjectMocks
    private DebtService debtService;

    @Test
    public void getDebtByParticipantIDTest() {
        Participant participant = new Participant();
        participant.setName("Jeff");
        Debt debt1 = new Debt();
        Debt debt2 = new Debt();
        debt1.setParticipant(participant);
        debt2.setParticipant(participant);

        when(participantRepository.findById(any(Long.class))).thenReturn(Optional.of(participant));
        when(debtRepository.getDebtsByParticipant(any(Participant.class))).thenReturn(List.of(debt1, debt2));

        List<Debt> debts = debtService.getDebtByParticipantID(42L);

        assertEquals(debts, List.of(debt1, debt2));
    }

    @Test
    public void getDebtByEventIDTest() {
        Event event = new Event();
        event.setId(13L);
        Expense expense = new Expense();
        expense.setId(2L);
        Debt debt1 = new Debt();
        Debt debt2 = new Debt();
        expense.setEvent(event);
        debt1.setExpense(expense);
        debt2.setExpense(expense);

//        when(eventRepository.findById(any(Long.class))).thenReturn(Optional.of(event));
        when(expenseRepository.getExpensesByEventId(13L)).thenReturn(List.of(expense));
        when(debtRepository.getDebtsByExpenseId(2L)).thenReturn(List.of(debt1, debt2));

        List<Debt> debts = debtService.getDebtByEventID(13L);

        assertEquals(debts, List.of(debt1, debt2));
    }

    @Test
    public void testCreateExpenseDebts() {
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("100"));
        Participant participant1 = new Participant();
        participant1.setId(1L);
        participant1.setEvent(new Event());
        Participant participant2 = new Participant();
        participant2.setId(2L);
        participant2.setEvent(new Event());
        List<Participant> participants = new ArrayList<>();
        participants.add(participant1);
        participants.add(participant2);
        List<Debt> result = new ArrayList<>();
        result = debtService.createExpenseDebts(expense, participants);

        assertNotNull(result);
        assertEquals(participants.size(), result.size());
    }

    @Test
    public void testUpdateDebt() {
        // Mock data
        Debt debt = new Debt();
        debt.setId(1L);
        BigDecimal newAmount = new BigDecimal("50");
        Boolean newPaid = true;
        when(debtRepository.findById(1L)).thenReturn(Optional.of(debt));
        when(debtRepository.save(any(Debt.class))).thenReturn(debt);
        // Test
        Debt updatedDebt = debtService.updateDebt(1L, newAmount, newPaid);

        // Verify
        assertNotNull(updatedDebt);
        assertEquals(newAmount, updatedDebt.getAmount());
        assertEquals(newPaid, updatedDebt.getPaid());
    }

    @Test
    public void testAddDebt() {
        Debt debt = new Debt();
        when(debtRepository.save(debt)).thenReturn(debt);
        Debt addedDebt = debtService.addDebt(debt);
        assertNotNull(addedDebt);
        assertEquals(debt, addedDebt);
    }

    @Test
    public void testGetDebtByExpenseId() {
        // Mock data
        Long expenseId = 1L;
        List<Debt> debts = new ArrayList<>();
        debts.add(new Debt());
        when(debtRepository.getDebtsByExpenseId(expenseId)).thenReturn(debts);

        // Test
        List<Debt> result = debtService.getDebtByExpenseId(expenseId);

        // Verify
        assertNotNull(result);
        assertEquals(debts, result);
    }

    @Test
    public void testDeleteDebt() {
        Long debtId = 1L;
        Debt debt = new Debt();
        debt.setId(debtId);
        when(debtRepository.findById(debtId)).thenReturn(Optional.of(debt));

        debtService.deleteDebt(debtId);

        //Verify
        verify(debtRepository, times(1)).deleteById(debtId);
    }
}
