package server.database;

import commons.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    @Query("SELECT t FROM Transfer t JOIN FETCH t.event e WHERE e.id = :eventId")
    List<Transfer> findByEventId(@Param("eventId") Long eventId);

//    @Query("SELECT t FROM Transfer t JOIN FETCH t.event e WHERE e.id = :eventId")
//    List<Transfer> findByEventId(@Param("eventId") Long eventId, @Param("debtId") Long debtId);

}
