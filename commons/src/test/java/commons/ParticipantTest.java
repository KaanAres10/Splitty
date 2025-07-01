package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {

    Participant p;
    Participant p1;

    @BeforeEach
    private void setup(){
        User u = new User("nik","en");
        Event e = new Event("test");
        p=new Participant(u, e , "nikola",
            "iban", "bic", "smth@gmail.com");
        p1=new Participant(u, e, "nikola",
            "iban", "bic", "smth@gmail.com");
    }

    @Test
    void getId() {
        assertNull(p.getId());
    }

    @Test
    void getEvent() {
        assertNotNull(p.getEvent());
        assertEquals(p.getEvent().getTitle(),"test");
    }

    @Test
    void getUser() {
        assertNotNull(p.getUser());
        assertEquals(p.getUser().getUsername(),"nik");

    }

    @Test
    void getName() {
        assertEquals(p.getName(),"nikola");
    }

    @Test
    void setName() {
        p.setName("gogo");
        assertEquals(p.getName(),"gogo");
    }

    @Test
    void getIban() {
        assertEquals(p.getIban(),"iban");
    }

    @Test
    void getBic() {
        assertEquals(p.getBic(),"bic");
    }

    @Test
    void setIban() {
        p.setIban("otherIban");
        assertEquals(p.getIban(),"otherIban");
    }

    @Test
    void setBic() {
        p.setBic("otherBic");
        assertEquals(p.getBic(),"otherBic");
    }

    @Test
    void getMail() {
        assertEquals(p.getMail(),"smth@gmail.com");
    }

    @Test
    void setMail() {
        p.setMail("other@gmail.com");
        assertEquals(p.getMail(),"other@gmail.com");
    }

    @Test
    void testEquals() {
        assertTrue(p.equals(p1));
    }

    @Test
    void testHashCode() {
        assertNotNull(p.hashCode());
    }

    @Test
    void setUser() {
        p.setUser(new User("gogo","en"));
        assertEquals(p.getUser().getUsername(),"gogo");
    }

    @Test
    void testToString() {
        assertEquals(p.toString(),"Name: nikola, Email: smth@gmail.com, IBAN: iban, BIC: bic, IsOwner: false");
    }
}