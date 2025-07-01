package server.database;

import commons.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    @Query("SELECT p FROM Participant p JOIN FETCH p.event e WHERE e.id = :eventId")
    List<Participant> findAllByEventId(@Param("eventId") Long eventId);
}

