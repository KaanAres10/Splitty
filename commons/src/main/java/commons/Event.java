package commons;

import jakarta.persistence.*;
import listeners.EntityListenerObservable;

import java.util.*;

@Entity
@EntityListeners(EntityListenerObservable.class)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastActivityDate;

    @Column(nullable = false, unique = true)
    private String inviteCode;


    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Participant> participants;

    @PrePersist
    public void prePersist() {
        // set the last activity date to now
        setLastActivityDate();
    }

    @PreUpdate
    public void preUpdate() {
        // just do the same thing as prePersist
        prePersist();
    }

    public Event() {
        this.creationDate = new Date();
        this.lastActivityDate = new Date();
        this.inviteCode = generateInviteCode();
        this.participants = new HashSet<>();
    }

    public Event(String title) {
        this();
        this.title = title;
    }

    /**
     * Constructor with fields required from the user
     * The other fields are generated!
     *
     * @param title      of the event
     * @param inviteCode of the event
     */
    public Event(String title, String inviteCode) {
        this.title = title;
        this.creationDate = new Date();
        this.lastActivityDate = new Date();
        this.inviteCode = inviteCode;
        this.participants = new HashSet<>();
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * @return the id of the event
     */
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the title of the event
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets new title to the Event
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the creation date of the event
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreationDate() {
        return creationDate;
    }


    /**
     * @return the date of the last activity of the event
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastActivityDate() {
        return lastActivityDate;
    }

    /**
     * Sets the last activity date
     */
    @Temporal(TemporalType.TIMESTAMP)
    public void setLastActivityDate() {
        this.lastActivityDate = new Date();
    }

    /**
     * @return the invite code for the event
     */
    public String getInviteCode() {
        return inviteCode;
    }

    /**
     * Sets new invite code for the event
     *
     * @param inviteCode for the event
     */
    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Set<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Participant> set) {
        this.participants = set;
    }


    /**
     * Adds new participant to the event
     *
     * @param p the participant to add
     */
    public void addParticipant(Participant p) {
        if (p != null) this.participants.add(p);
    }

    /**
     * Removes a participant if present in the set of participants
     *
     * @param p the participant to be removed
     * @return the removed participant or null if not present in the set
     */
    public Participant removeParticipant(Participant p) {
        if (p != null && this.participants.remove(p)) {
            return p;
        } else return null;
    }

    /**
     * Checks if an object is equal to the event
     *
     * @param o the object to compare with
     * @return the
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;
        return Objects.equals(id, event.id) && Objects.equals(getTitle(), event.getTitle()) &&
                Objects.equals(getCreationDate(), event.getCreationDate()) &&
                Objects.equals(getLastActivityDate(), event.getLastActivityDate()) &&
                Objects.equals(getInviteCode(), event.getInviteCode()) &&
                Objects.equals(participants, event.participants);
    }

    /**
     * @return the hash code of the event
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                title,
                creationDate,
                lastActivityDate,
                inviteCode,
                participants
        );
    }

    /**
     * @return a String representation of the event
     */
    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", creationDate=" + creationDate +
                ", lastActivityDate=" + lastActivityDate +
                ", inviteCode='" + inviteCode + '\'' +
                '}';
    }
}
