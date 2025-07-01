package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransferTest {

    Transfer transfer;
    Transfer transfer1;
    Debt debt;

    @BeforeEach
    private void setup() {
        Event event = new Event("test");
        Participant p = new Participant(new User(), event);
        debt = new Debt(new Expense(), p, new BigDecimal(12), false, p, event);
        transfer = new Transfer(debt, false, "msg", event);
        transfer1 = new Transfer(debt, false, "msg", event);
    }

    @Test
    void getId() {
        assertNull(transfer.getId());
    }

    @Test
    void setId() {
        long a = 6;
        transfer.setId(a);
        assertEquals(transfer.getId(), 6);
    }

    @Test
    void getDebt() {
        assertNotNull(transfer.getDebt());
        assertEquals(transfer.getDebt(), debt);
    }

    @Test
    void setDebt() {
        transfer.setDebt(new Debt());
        assertNotEquals(transfer.getDebt(), debt);
    }

    @Test
    void isApproved() {
        assertFalse(transfer.isApproved());
    }

    @Test
    void setApproved() {
        transfer.setApproved(true);
        assertTrue(transfer.isApproved());
    }

    @Test
    void getMessage() {
        assertEquals(transfer.getMessage(), "msg");
    }

    @Test
    void setMessage() {
        transfer.setMessage("other");
        assertEquals(transfer.getMessage(), "other");
    }

    @Test
    void testToString() {
        assertEquals(transfer.toString(),
            "Transfer{id=null, debt=" + debt + ", approved=false, message='msg'}");
    }

    @Test
    void testEquals() {
        assertTrue(transfer.equals(transfer1));
    }

    @Test
    void testHashCode() {
        assertNotNull(transfer.hashCode());
    }
}