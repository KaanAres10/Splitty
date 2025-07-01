package server.database;

import commons.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT t FROM Tag t JOIN FETCH t.event e WHERE e.id = :eventId")
    List<Tag> findTagsByEventId(@Param("eventId") Long eventId);

}
