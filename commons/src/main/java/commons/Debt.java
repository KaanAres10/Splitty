package commons;

import jakarta.persistence.*;
import listeners.EntityListenerObservable;
import java.math.BigDecimal;


/**
 * Entity representing a Debt, which is an amount of money that one participant owes to another in relation to an expense.
 */
@Entity
@EntityListeners(EntityListenerObservable.class)
public class Debt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "debt_id")
    private Long id;

    /**
     * The expense related to this debt.
     */
    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    /**
     * The participant who owes the amount.
     */
    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    /**
     * The amount of money owed.
     */
    private BigDecimal amount;

    /**
     * Indicates whether the debt has been paid or not.
     */
    private Boolean paid;

    /**
     * The participant who is to receive the payment of the debt.
     */
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Participant receiver;

    @ManyToOne
    @JoinColumn (name = "event_id")
    private Event event;

    /**
     * Instantiates a new Debt.
     */
    public Debt() {
    }

    public Debt(Expense expense, Participant participant, BigDecimal amount,
                Boolean paid, Participant receiver, Event event) {
        this.expense = expense;
        this.participant = participant;
        this.amount = amount;
        this.paid = paid;
        this.receiver = receiver;
        this.event = event;
    }

    public Debt(Debt d) {
        this.id = d.id;
        this.expense = d.expense;
        this.participant = d.participant;
        this.amount = d.amount;
        this.paid = d.paid;
        this.receiver = d.receiver;
        this.event = d.event;
    }

    protected Debt(Long id, Expense expense, Participant participant, BigDecimal amount, Boolean paid, Participant receiver) {
        this.id = id;
        this.expense = expense;
        this.participant = participant;
        this.amount = amount;
        this.paid = paid;
        this.receiver = receiver;
    }

    /**
     * Gets the unique identifier for the debt.
     *
     * @return the unique identifier for the debt
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the debt.
     *
     * @param id the new unique identifier for the debt
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the associated expense.
     *
     * @return the associated expense
     */
    public Expense getExpense() {
        return expense;
    }

    /**
     * Sets the associated expense.
     *
     * @param expense the expense related to this debt
     */
    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    /**
     * Gets the participant who owes the debt.
     *
     * @return the owing participant
     */
    public Participant getParticipant() {
        return participant;
    }

    /**
     * Sets the participant who owes the debt.
     *
     * @param participant the owing participant
     */
    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    /**
     * Gets the amount of the debt.
     *
     * @return the amount owed
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the debt.
     *
     * @param amount the amount owed
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Checks if the debt is paid.
     *
     * @return true if paid, false otherwise
     */
    public Boolean getPaid() {
        return paid;
    }

    /**
     * Marks the debt as paid or unpaid.
     *
     * @param paid the payment status of the debt
     */
    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    /**
     * Gets the receiver participant of the debt.
     *
     * @return the participant who is to receive the payment
     */
    public Participant getReceiver() {
        return receiver;
    }

    /**
     * Sets the receiver participant of the debt.
     *
     * @param receiver the participant who is to receive the payment
     */
    public void setReceiver(Participant receiver) {
        this.receiver = receiver;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
