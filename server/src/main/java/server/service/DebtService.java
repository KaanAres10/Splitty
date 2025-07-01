package server.service;

import commons.Debt;
import commons.Expense;
import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.EventLastActivityListener;
import server.database.DebtRepository;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DebtService {

    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final ExpenseRepository expenseRepository;
    private final DebtRepository debtRepository;
    private final EventLastActivityListener eventLastActivityListener;

    @Autowired
    public DebtService(ParticipantRepository participantRepository,
                       EventRepository eventRepository,
                       ExpenseRepository expenseRepository,
                       DebtRepository debtRepository,
                       EventLastActivityListener eventLastActivityListener) {
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
        this.expenseRepository = expenseRepository;
        this.debtRepository = debtRepository;
        this.eventLastActivityListener = eventLastActivityListener;
    }

    public List<Debt> getDebtByParticipantID(Long id) {
        // Validate the ParticipantID
        if (id == null) return null;
        Optional<Participant> participant = participantRepository.findById(id);
        if (participant.isEmpty()) return null;

        return debtRepository.getDebtsByParticipant(participant.get());
    }

    public List<Debt> getDebtByEventID(Long id) {
        List<Expense> expenses = expenseRepository.getExpensesByEventId(id);
        List<Debt> debts = new ArrayList<>();
        for (Expense expense : expenses) {
            // this might be very slow, but I haven't tested it.
            List<Debt> expenseDebts = debtRepository.getDebtsByExpenseId(expense.getId());
            debts.addAll(expenseDebts);
        }
        return debts;
    }

    public List<Debt> createExpenseDebts(Expense expense, List<Participant> participants) {
        if (expense == null || participants == null) return null;
        List<Debt> debts = new ArrayList<>(participants.size());
        if (participants.isEmpty()) return debts;

        // determine the amount rounded to 2 decimal places
        // (this might not be correct for every currency since not every currency is subdividable into cents.
        // It also could lose precision for certain amounts.)
        BigDecimal amount = expense.getAmount().divide(
                BigDecimal.valueOf(participants.size()),
                2,
                RoundingMode.HALF_UP
        );
        for (Participant participant : participants) {
            Debt debt = new Debt(
                    expense,
                    participant,
                    amount,
                    false,
                    expense.getPayer(),
                    participant.getEvent()
            );

            debts.add(addDebt(debt));

        }

        return debts;
    }

    public Debt updateDebt(Long id, BigDecimal newAmount, Boolean newPaid) {
        Debt debt = debtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No debt with provided debt_id in database"));

        // null values represent no change
        if (newAmount != null) debt.setAmount(newAmount);
        if (newPaid != null) debt.setPaid(newPaid);

        Debt out = debtRepository.save(debt);
        eventLastActivityListener.postUpdate(out);
        return out;
    }
    public void deleteDebt(Long id){
        Debt debt = debtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No debt with provided debt_id in database"));

        debtRepository.deleteById(id);
        eventLastActivityListener.postRemove(debt);
    }

    public Debt addDebt(Debt debt) {
        Debt out = debtRepository.save(debt);
        eventLastActivityListener.postPersist(out);
        return out;
    }

    public List<Debt> getDebtByExpenseId(Long expenseId) {
        return debtRepository.getDebtsByExpenseId(expenseId);
    }

}
