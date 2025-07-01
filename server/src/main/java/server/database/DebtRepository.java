package server.database;

import commons.Debt;
import commons.Expense;
import commons.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {

    List<Debt> getDebtsByExpenseId(Long expenseId);
    List<Debt> getDebtsByExpense(Expense expense);
    List<Debt> getDebtsByParticipant(Participant participant);

    @Query("SELECT d FROM Debt d JOIN FETCH d.event e WHERE e.id = :eventId")
    List<Debt> findByEventId(@Param("eventId") Long eventId);
    @Query("SELECT d FROM Debt d JOIN FETCH d.event e WHERE e.id IS NULL")
    List<Debt> findWithNullEvent();
    @Query("SELECT d FROM Debt d")
    List<Debt> getAllDebts();

}
