package commons;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Entity
@Table(name = "Tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tag_id")
    private Long id;

    private String name;
    private String color;

    // Expenses associated with this tag
    @ManyToMany(mappedBy = "tags")
    private Set<Expense> expenses;
    
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    /**
     * Empty constructor
     */
    public Tag() {
    }

    /**
     * Constructor with tag name, and tag color
     *
     * @param name  the name of the tag
     * @param color the color of the tag
     */
    public Tag(String name, String color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Constructor with tag name, tag color, and associated expenses
     *
     * @param name     the name of the tag
     * @param color    the color of the tag
     * @param expenses the expenses associated with the tag
     */
    public Tag(String name, String color, Set<Expense> expenses, Event event) {
        this.name = name;
        this.color = color;
        this.expenses = expenses;
        this.event = event;
    }

    public Tag(Tag t) {
        this.id = t.getId();
        this.name = t.getName();
        this.color = t.getColor();
        this.expenses = t.getExpenses();
        this.event = t.getEvent();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Set<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(Set<Expense> expenses) {
        this.expenses = expenses;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(getId(), tag.getId())
                && Objects.equals(getName(), tag.getName())
                && Objects.equals(getColor(), tag.getColor())
                && Objects.equals(expenses, tag.expenses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getColor(), expenses);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", expenses=" + expenses +
                '}';
    }
}
