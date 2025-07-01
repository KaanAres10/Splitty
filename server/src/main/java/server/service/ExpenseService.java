package server.service;

import commons.Debt;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.EventLastActivityListener;
import server.database.DebtRepository;
import server.database.ExpenseRepository;
import server.database.TagRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository db;

    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EventLastActivityListener eventLastActivityListener;


    public Expense getById(long id) {
        return db.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    public Expense addExpense(Expense expense) {
        Expense ret = db.save(expense);
        eventLastActivityListener.postPersist(ret);
        return ret;
    }

    public Expense addExpenseWithoutDebts(Expense expense) {
        List<Participant> participants = expense.getParticipants();
        expense.setParticipants(null);
        expense = db.save(expense);
        expense.setParticipants(participants);
        eventLastActivityListener.postPersist(expense);
        return expense;
    }

    public boolean deleteExpense(Expense expense) {
        Optional<Expense> optionalExpense = db.findById(expense.getId());
        if (optionalExpense.isPresent()) {
            db.delete(expense);
            return true;
        } else {
            return false;
        }
    }

    public Expense updateExpenseDetails(Expense expense) {
        // todo: refactor
        if (expense.getTags() != null) {
            for (Tag t : expense.getTags()) {
                if (t.getExpenses() == null) {
                    t.setExpenses(new HashSet<>());
                }
                t.getExpenses().add(expense);
                Tag ret = tagRepository.save(t);
                eventLastActivityListener.postUpdate(ret);
            }
        }

        Expense finalExpense;
        if (db.findById(expense.getId()).isPresent()) {
            finalExpense = db.save(expense);
            eventLastActivityListener.postUpdate(finalExpense);
        } else {
            finalExpense = null;
        }
        List<Debt> oldDebts = debtRepository.getDebtsByExpense(finalExpense);
        Set<Participant> removedParticipants = oldDebts.stream()
                .map(Debt::getParticipant)
                .filter(participant -> !finalExpense.getParticipants().contains(participant))
                .collect(Collectors.toCollection(HashSet::new));
        // remove all the debts of the removedParticipants
        for (Debt debt : oldDebts) {
            if (removedParticipants.contains(debt.getParticipant())) {
                debtRepository.delete(debt);
                eventLastActivityListener.postRemove(debt);
            }
        }

        // constant lookup time isn't really necessary but ah
        Map<Participant, Debt> participantDebtMap = new HashMap<>();
        for (Debt debt : oldDebts) {
            participantDebtMap.put(debt.getParticipant(), debt);
        }

        BigDecimal amount = finalExpense.getAmount().divide(
                BigDecimal.valueOf(finalExpense.getParticipants().size()),
                2,
                RoundingMode.HALF_UP
        );
        for (Participant participant : finalExpense.getParticipants()) {
            Debt debt = participantDebtMap.getOrDefault(participant, new Debt(
                    finalExpense,
                    participant,
                    null,
                    false,
                    finalExpense.getPayer(),
                    finalExpense.getEvent()
            ));
            debt.setAmount(amount);
            debt.setReceiver(finalExpense.getPayer());
            Debt ret = debtRepository.save(debt);
            eventLastActivityListener.postUpdate(ret);
        }
        return expense;

    }

    public List<Expense> getAll(Long eventId) {
        return db.getExpensesByEventId(eventId);
    }
}
