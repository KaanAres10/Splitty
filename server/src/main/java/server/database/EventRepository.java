package server.database;

import commons.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByInviteCode(String inviteCode);

    @Query("SELECT e FROM Event e JOIN FETCH e.participants p WHERE p.user.id = :userId")
    List<Event> findByParticipantsContains(@Param("userId") Long userId);

    @Query("SELECT e FROM Event e")
    List<Event> getAllEvents();
}
