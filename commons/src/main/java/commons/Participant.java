package commons;

import jakarta.persistence.*;
import listeners.EntityListenerObservable;

import java.util.Objects;

@Entity
@EntityListeners(EntityListenerObservable.class)
public class Participant {

    /**
     * unique id for an added participant
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "participant_id")
    private Long id;

    /**
     * id of the event participant was added to
     */
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    /**
     * id of user that belongs to the participant
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * name set to the added participant
     */
    @Column(name = "participant_name")
    private String name;

    /**
     * bank info set to added participant
     */
    @Column(name = "IBAN")
    private String iban;
    @Column(name = "BIC")
    private String bic;

    /**
     * mail info set for added participant
     */
    private String mail;

    private boolean isOwner;

    /**
     * Empty constructor for participant
     */
    public Participant() {
    }

    /**
     * Basic constructor for participant
     *
     * @param event event participant is added to
     * @param user  user that belongs to participant
     * @param name  name set for participant
     * @param iban  iban for participant
     * @param bic   bic for participant
     * @param mail  mail info set for participant
     */
    public Participant(User user, Event event, String name,
                       String iban, String bic, String mail) {
        this.user = user;
        this.event = event;
        this.name = name;
        this.iban = iban;
        this.bic = bic;
        this.mail = mail;
    }

    public Participant(Participant participantToCopy) {
        this.user = participantToCopy.getUser();
        this.event = participantToCopy.getEvent();
        this.id = participantToCopy.getId();
        this.name = participantToCopy.getName();
        this.iban = participantToCopy.getIban();
        this.bic = participantToCopy.getBic();
        this.mail = participantToCopy.getMail();
        this.isOwner = participantToCopy.isOwner();
    }

    public Participant(User user, Event event) {
        this.user = user;
        this.event = event;
    }

    /**
     * gets the unique id from the participant
     *
     * @return the id of the participant
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * gets the id of the event the participant belongs to
     *
     * @return id of participant
     */
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * gets the id of the user who is now participant
     *
     * @return id of the user
     */
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * gets the name for the added participant
     *
     * @return name of participant
     */
    public String getName() {
        return name;
    }

    /**
     * setter for name of an added participant
     *
     * @param name is name of participant
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getter for the bank info of a participant
     *
     * @return bank account of participant
     */
    public String getIban() {
        return iban;
    }

    /**
     * setter of bank account for an added participant
     *
     * @param iban to be used as bank account info
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    /**
     * getter for mail address associated with participant
     *
     * @return mail from participant
     */
    public String getMail() {
        return mail;
    }

    /**
     * sets the mail of an added participant
     *
     * @param eMail is mail to be set as mail info
     */
    public void setMail(String eMail) {
        this.mail = eMail;
    }

    /**
     * equals method to check equality among participants
     *
     * @param o object to compare with
     * @return true if equal, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return Objects.equals(id, that.id);
    }

    /**
     * method for hashing a participant
     *
     * @return hashed form af participant
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, event, user, name, iban, bic, mail, isOwner);
    }

    @Override
    public String toString() {
        String s = "";
        s += "Name: " + name + ", ";
        s += "Email: " + mail + ", ";
        s += "IBAN: " + iban + ", ";
        s += "BIC: " + bic + ", ";
        s += "IsOwner: " + isOwner;
        return s;
    }
}
