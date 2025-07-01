package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    User u;
    User u1;

    @BeforeEach
    private void setup() {
        u = new User("nik", "eng");
        u1 = new User("nik", "eng");
    }

    @Test
    void getId() {
        assertNull(u.getId());
    }

    @Test
    void setId() {
        long a = 6;
        u.setId(a);
        assertEquals(u.getId(),6);
    }

    @Test
    void getUsername() {
        assertEquals(u.getUsername(),"nik");
    }

    @Test
    void setUsername() {
        u.setUsername("nikola");
        assertEquals(u.getUsername(),"nikola");
    }

    @Test
    void getPreferredLanguage() {
        assertEquals(u.getPreferredLanguage(),"eng");
    }

    @Test
    void setPreferredLanguage() {
        u.setPreferredLanguage("dutch");
        assertEquals(u.getPreferredLanguage(),"dutch");
    }

    @Test
    void testToString() {
        assertEquals(u.toString(),"User{id=null, username='nik', preferredLanguage='eng'}");
    }

    @Test
    void testEquals() {
        assertTrue(u.equals(u1));
    }

    @Test
    void testHashCode() {
        assertNotNull(u.hashCode());
    }
}