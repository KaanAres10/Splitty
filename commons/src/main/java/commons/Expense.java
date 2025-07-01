package commons;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import listeners.EntityListenerObservable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")

@Entity
@EntityListeners(EntityListenerObservable.class)
public class Expense {
    /**
     * The id of this expense
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "expense_id")
    private Long id;
    /**
     * The event where the expense was made
     */
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    /**
     * The title of this expense
     */
    private String title;
    /**
     * The amount of this expense
     */
    private BigDecimal amount;
    /**
     * Date when the expense was made
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    /**
     * Participant that paid this expense
     */
    @ManyToOne
    @JoinColumn(name = "payer_id", nullable = false)
    private Participant payer;
    /**
     * Currency of the expense
     */
    private String currency;

    @ManyToMany
    @JoinTable(
            name = "expense_tag",
            joinColumns = @JoinColumn(name = "expense_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "expense_participant", // Name of the join table
            joinColumns = @JoinColumn(name = "expense_id"), // Foreign key for Expense in expense_tag table
            inverseJoinColumns = @JoinColumn(name = "participant_id") // Foreign key for Tag in expense_tag table
    )
    private List<Participant> participants;

    /**
     * A constructor for Expense
     */
    public Expense(Event event, String title, BigDecimal amount, Participant payer, Date date, String currency,
                   List<Tag> tags, List<Participant> participants) {
        this.event = event;
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.payer = payer;
        this.currency = currency;
        this.tags = tags;
        this.participants = participants;
    }

    /**
     * An empty constructor for the Expense
     */
    public Expense() {

    }

    public Expense(Expense e) {
        this.id = e.getId();
        this.event = e.getEvent();
        this.title = e.title;
        this.amount = e.amount;
        this.date = e.date;
        this.payer = e.payer;
        this.currency = e.currency;
        this.tags = e.tags;
        this.participants = e.participants;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> newParticipants) {
        this.participants = newParticipants;
    }

    /**
     * Get method for id of the expense
     *
     * @return the unique identifier for the expense
     */
    public Long getId() {
        return id;
    }

    /**
     * Set method for id of the expense
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get method for Event where the expense was made
     *
     * @return the Event where the expense was made
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Set method for Event where the expense was made
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Get method for Title of the expense
     *
     * @return the Title of the expense
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set method for Title of the expense
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get method for amount of the expense
     *
     * @return amount of the expense
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Set method for amount of the expense
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Get method for date of the expense
     *
     * @return date of the expense
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set method for the date of the expense
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Get method for Payer of the expense
     *
     * @return Payer of the expense
     */
    public Participant getPayer() {
        return payer;
    }

    /**
     * Set method for the Payer of the expense
     */
    public void setPayer(Participant payer) {
        this.payer = payer;
    }

    /**
     * Get method for currency of the expense
     *
     * @return currency of the expense
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Set method for currency of the expense
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * An equals method for the expense
     *
     * @param o - second expense
     * @return true if they are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id);
    }


    /**
     * A hashcode method for the expense class
     *
     * @return a hashcode for the expense object
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}