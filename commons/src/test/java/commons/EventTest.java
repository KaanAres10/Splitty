package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    private Event event;
    private Event sameEvent;

    @Test
    public void testInviteCodeGeneration() {
        Event event = new Event("Sample Event");
        assertNotNull(event.getInviteCode(), "Invite code should not be null");
        assertEquals(8, event.getInviteCode().length(), "Invite code should be 8 characters long");
        assertTrue(event.getInviteCode().matches("[a-fA-F0-9]+"), "Invite code should be in expected format");
    }

    @BeforeEach
    public void setUp() {
        this.event = new Event("test");
        this.sameEvent = this.event;
        Set<Participant> set = new HashSet<>();
        set.add(new Participant());
        event.setParticipants(set);
        sameEvent.setParticipants(set);
        sameEvent.setInviteCode(event.getInviteCode());
        sameEvent.setId(event.getId());
    }

    @Test
    void getId() {
        assertNull(event.getId());
    }

    @Test
    void getTitle() {
        assertEquals(event.getTitle(), "test");
    }

    @Test
    void setTitle() {
        event.setTitle("new");
        assertEquals(event.getTitle(), "new");
    }

    @Test
    void getCreationDate() {
        assertNotNull(event.getCreationDate());
    }

    @Test
    void getLastActivityDate() {
        assertNotNull(event.getLastActivityDate());
    }

    @Test
    void setLastActivityDate() {
        event.setLastActivityDate();
        assertNotNull(event.getLastActivityDate());
    }

    @Test
    void getInviteCode() {
        assertNotNull(event.getInviteCode());
    }

    @Test
    void setInviteCode() {
        event.setInviteCode("123");
        assertEquals(event.getInviteCode(), "123");

    }

    @Test
    void addParticipant() {
        event.addParticipant(new Participant(new User(), event));
        assertEquals(event.getParticipants().size(), 2);
    }


    @Test
    void getParticipants() {
        assertEquals(event.getParticipants().size(), 1);
    }

    @Test
    void setParticipants() {
        Set<Participant> set = new HashSet<>();
        set.add(new Participant(new User(), event));
        event.setParticipants(set);
        assertEquals(event.getParticipants().size(), 1);

    }

    @Test
    void removeParticipant() {
        event.removeParticipant(new Participant());
        assertEquals(event.getParticipants().size(), 0);
    }

    @Test
    void testEquals() {
        assertNotEquals(event, new Event());
        assertEquals(event, sameEvent);
    }

    @Test
    void testHashCode() {
        assertNotNull(event.hashCode());
    }

    @Test
    void testToString() {
        assertEquals(event.toString(), "Event{" +
                "title='" + event.getTitle() + '\'' +
                ", creationDate=" + event.getCreationDate() +
                ", lastActivityDate=" + event.getLastActivityDate() +
                ", inviteCode='" + event.getInviteCode() + '\'' +
                '}');
    }
}