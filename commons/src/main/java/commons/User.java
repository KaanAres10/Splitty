package commons;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false)
    private Long id;
    private String username;
    private String preferredLanguage;


    public User() {
        this.preferredLanguage = "en"; // Set the default value directly in the constructor
        this.username = "";
    }

    public User(String username, String preferredLanguage) {
        this.username = username;
        this.preferredLanguage = preferredLanguage;
    }

    public Long getId() {
        return id;
    }

    public void setId(long l) {
        this.id = l;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", preferredLanguage='" + preferredLanguage + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(preferredLanguage, user.preferredLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, preferredLanguage);
    }
}
