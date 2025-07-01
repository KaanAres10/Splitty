package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DebtTest {

    private Debt debt;
    private Expense expense;
    private Participant participant;
    private Participant receiver;
    private Event event;
    private BigDecimal amount;

    @BeforeEach
    void setUp() {
        expense = new Expense(); // Assuming constructor and necessary setters
        participant = new Participant(); // Assuming constructor and necessary setters
        receiver = new Participant(); // Assuming constructor and necessary setters
        event = new Event(); // Assuming constructor and necessary setters
        amount = new BigDecimal("100.00");
        debt = new Debt(expense, participant, amount, false, receiver, event);
    }

    @Test
    void testConstructorWithParameters() {
        assertSame(expense, debt.getExpense());
        assertSame(participant, debt.getParticipant());
        assertEquals(amount, debt.getAmount());
        assertFalse(debt.getPaid());
        assertSame(receiver, debt.getReceiver());
        assertSame(event, debt.getEvent());
    }

    @Test
    void testCopyConstructor() {
        Debt newDebt = new Debt(debt);
        assertEquals(debt.getId(), newDebt.getId());
        assertSame(debt.getExpense(), newDebt.getExpense());
        assertSame(debt.getParticipant(), newDebt.getParticipant());
        assertEquals(debt.getAmount(), newDebt.getAmount());
        assertEquals(debt.getPaid(), newDebt.getPaid());
        assertSame(debt.getReceiver(), newDebt.getReceiver());
        assertSame(debt.getEvent(), newDebt.getEvent());
    }

    @Test
    void testSetAndGetId() {
        debt.setId(1L);
        assertEquals(1L, debt.getId());
    }

    @Test
    void testSetAndGetExpense() {
        Expense newExpense = new Expense();
        debt.setExpense(newExpense);
        assertSame(newExpense, debt.getExpense());
    }

    @Test
    void testSetAndGetParticipant() {
        Participant newParticipant = new Participant();
        debt.setParticipant(newParticipant);
        assertSame(newParticipant, debt.getParticipant());
    }

    @Test
    void testSetAndGetAmount() {
        BigDecimal newAmount = new BigDecimal("200.00");
        debt.setAmount(newAmount);
        assertEquals(newAmount, debt.getAmount());
    }

    @Test
    void testSetAndGetPaid() {
        debt.setPaid(true);
        assertTrue(debt.getPaid());
    }

    @Test
    void testSetAndGetReceiver() {
        Participant newReceiver = new Participant();
        debt.setReceiver(newReceiver);
        assertSame(newReceiver, debt.getReceiver());
    }

    @Test
    void testSetAndGetEvent() {
        Event newEvent = new Event();
        debt.setEvent(newEvent);
        assertSame(newEvent, debt.getEvent());
    }

    @Test
    void testSetExpenseWithNull() {
        Debt debt = new Debt();
        debt.setExpense(null);
        assertNull(debt.getExpense(), "Expense should handle null values gracefully");
    }

    @Test
    void testSetAmountWithNull() {
        Debt debt = new Debt();
        debt.setAmount(null);
        assertNull(debt.getAmount(), "Amount should handle null values gracefully");
    }

    @Test
    void testSetPaidWithNull() {
        Debt debt = new Debt();
        debt.setPaid(null);
        assertNull(debt.getPaid(), "Paid status should handle null values gracefully");
    }

}
