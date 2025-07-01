package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class ExpenseTest {

    @Test
    public void checkGetAndSetId() {
        Expense e = new Expense();
        e.setId(228L);
        assertEquals(228L, e.getId());
        e.setId(230L);
        assertEquals(230L, e.getId());
    }

    @Test
    public void checkGetAndSetTitle() {
        Expense e = new Expense();
        e.setTitle("Food");
        assertEquals("Food", e.getTitle());
        e.setTitle("Movie");
        assertEquals("Movie", e.getTitle());
    }

    @Test
    public void checkGetAndSetAmount() {
        Expense e = new Expense();
        e.setAmount(BigDecimal.valueOf(200));
        assertEquals(BigDecimal.valueOf(200), e.getAmount());
        e.setAmount(BigDecimal.valueOf(230));
        assertEquals(BigDecimal.valueOf(230), e.getAmount());
    }

    @Test
    public void checkGetAndSetCurrency() {
        Expense e = new Expense();
        e.setCurrency("dollar");
        assertEquals("dollar", e.getCurrency());
        e.setCurrency("euro");
        assertEquals("euro", e.getCurrency());
    }
}