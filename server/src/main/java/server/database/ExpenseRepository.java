package server.database;

import commons.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense,Long> {

    List<Expense> getExpensesByEventId(Long eventId);
    @Query("SELECT ex FROM Expense ex JOIN FETCH ex.event e WHERE e.id = :eventId")
    List<Expense> findByEventId(@Param("eventId") Long eventId);

}