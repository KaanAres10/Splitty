package commons;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "transfer_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="debt_id", nullable = false)
    private Debt debt;
    @Column (nullable = false)
    private boolean approved;
    @Column (nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn (name = "event_id", nullable = false)
    private Event event;
    public Transfer(){

    }
    public Transfer(Debt debt, boolean approved, String message, Event event) {
        this.debt = debt;
        this.approved = approved;
        this.message = message;
        this.event = event;
    }

    public Transfer(Long id, Debt debt, boolean approved, String message) {
        this.id = id;
        this.debt = debt;
        this.approved = approved;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Debt getDebt() {
        return debt;
    }

    public void setDebt(Debt debt) {
        this.debt = debt;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", debt=" + debt +
                ", approved=" + approved +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return approved == transfer.approved && Objects.equals(id, transfer.id) &&
                Objects.equals(debt, transfer.debt) && Objects.equals(message, transfer.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, debt, approved, message);
    }


}
