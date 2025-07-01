package server.api;

import commons.Event;
import commons.Expense;
import commons.Tag;
import commons.Debt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.DebtService;
import server.service.EventService;
import server.service.ExpenseService;
import server.service.TagService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping ("/api/events/{eventId}/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private DebtService debtService;

    @Autowired
    private EventService eventService;

    @Autowired
    private TagService tagService;

    @Autowired
    private WebSocketUtil webSocketUtil;

    @GetMapping ("/{id}")
    public Expense getById(@PathVariable ("id") long id) {
        return expenseService.getById(id);
    }

    @GetMapping
    public List<Expense> getAllByEvent(@PathVariable Long eventId) {
        return expenseService.getAll(eventId);
    }


    @PostMapping ("/create")
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense) {
        if (expense.getTitle() == null || expense.getAmount() == null ||
            expense.getCurrency() == null
            || expense.getPayer() == null || expense.getEvent() == null ||
            expense.getDate() == null) {
            return ResponseEntity.badRequest().build();
        }

        Expense saved = expenseService.addExpense(expense);
        debtService.createExpenseDebts(saved, expense.getParticipants());

        return ResponseEntity.ok(saved);
    }

    @PostMapping ("/create/nodebts")
    public ResponseEntity<Expense> addExpenseWithoutDebts(@RequestBody Expense expense) {
        if (expense.getTitle() == null || expense.getAmount() == null ||
            expense.getTitle() == "" || expense.getCurrency() == null
            || expense.getPayer() == null || expense.getEvent() == null ||
            expense.getDate() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(expenseService.addExpense(expense));
    }

    @DeleteMapping ("/delete/{id}")
    public ResponseEntity<Expense> deleteExpense(@PathVariable ("id") long id) {
        Expense expense = expenseService.getById(id);
        if (expense == null) {
            return ResponseEntity.notFound().build();
        } else {
            Long eventId = expense.getEvent().getId();
            expenseService.addExpense(expense);
            List<Tag> tags = expense.getTags();
            expense.setTags(new ArrayList<>());
            for(Tag t: tags){
                String name = t.getName();
                t.getExpenses().remove(expense);
                if(!t.getExpenses().isEmpty()
                        || Objects.equals(name, "Food")
                        || Objects.equals(name, "Travel")
                        || Objects.equals(name, "Entrance Fees"))
                    t = tagService.addTag(eventId, t);
                else
                    tagService.removeTag(eventId, t.getId());
            }
            List<Debt> debts = debtService.getDebtByExpenseId(expense.getId());
            for(Debt debt: debts){
                debtService.deleteDebt(debt.getId());
            }
            tags = eventService.getAllTagsByEventId(eventId);
            expenseService.deleteExpense(expense);

            return ResponseEntity.ok(expense);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Expense>> getAllExpensesByEventId(@PathVariable("eventId") long eventId) {
        Event event = eventService.findById(eventId);
        if (event == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(eventService.getAllExpensesByEventId(eventId));
    }

    @PutMapping ("/edit")
    public ResponseEntity<Expense> editExpense(@RequestBody Expense expense) {
        Expense retrieved = expenseService.getById(expense.getId());
        if (retrieved == null) {
            return ResponseEntity.notFound().build();
        }
        if (expense.getPayer() == null || expense.getAmount() == null
            || expense.getParticipants() == null || expense.getTitle() == null ||
            expense.getTitle() == "" || expense.getEvent() == null) {
            return ResponseEntity.badRequest().build();
        } else {
            List<Debt> debts = debtService.getDebtByExpenseId(retrieved.getId());
            for(Debt d : debts){
                debtService.deleteDebt(d.getId());
            }
            expenseService.updateExpenseDetails(expense);
            return ResponseEntity.ok(expense);
        }
    }
}