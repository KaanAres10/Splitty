package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {

    private static Tag tag1;
    private static Tag tag2;
    private static Tag tag3;
    @BeforeEach
    void setUp() {
        tag1 = new Tag();
        tag2 = new Tag("Food", "Red");
        tag3 = new Tag("Utilities", "Yellow", new HashSet<Expense>(), null);
    }
    @Test
    void testIDGenerator() {
        assertNull(tag1.getId());
    }

    @Test
    void testConstructorAndGetName() {
        assertEquals(tag2.getName(), "Food");
    }

    @Test
    void testSettersAndGetters() {
        tag1.setName("Water");
        tag1.setColor("Blue");
        assertEquals(tag1.getColor() + " " + tag1.getName(), "Blue Water");
    }

    @Test
    void testEquals() {
        tag2.setName("Utilities");
        tag2.setColor("Yellow");
        tag2.setExpenses(new HashSet<Expense>());
        assertEquals(tag2, tag3);
    }
}