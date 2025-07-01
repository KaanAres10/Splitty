package server.api;

import commons.Debt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import server.service.DebtService;

import java.util.List;

/**
 * A controller to handle requests related to participants debts related to a specific event.
 * This controller only implements Get methods since a Debt record should only change if another
 * record, like the related Expense, is changed.
 */
@RestController
@RequestMapping("/api/events/{eventId}/debts")
public class DebtController {

    private final DebtService debtService;

    @Autowired
    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }

    @GetMapping("/participants/{participantId}")
    public ResponseEntity<List<Debt>> getParticipantsDebt(@PathVariable Long eventId,
                                                          @PathVariable Long participantId) {
        if (eventId == null || participantId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Debt> debts = debtService.getDebtByParticipantID(participantId);
        if (debts == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(debts);
    }

    @GetMapping
    public ResponseEntity<List<Debt>> getEventDebt(@PathVariable Long eventId) {
        if (eventId == null) return ResponseEntity.badRequest().build();

        List<Debt> debts = debtService.getDebtByEventID(eventId);
        if (debts == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(debts);
    }

    @PutMapping("/{debtId}")
    public ResponseEntity<Debt> updatePaid(@PathVariable Long debtId,
                                           @RequestParam Boolean paid) {
        if (debtId == null || paid == null) return ResponseEntity.badRequest().build();
        try {
            Debt debt = debtService.updateDebt(debtId, null, paid);
            return ResponseEntity.ok(debt);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<List<Debt>> getDebtsByExpense(@PathVariable Long expenseId) {
        if (expenseId == null) return ResponseEntity.badRequest().build();
        List<Debt> debts = debtService.getDebtByExpenseId(expenseId);
        if (debts == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(debts);
    }
    @PostMapping("/add")
    public ResponseEntity<Debt> addDebt(@RequestBody Debt debt) {
        if (debt == null) return ResponseEntity.badRequest().build();
        try {
            Debt d = debtService.addDebt(debt);
            return ResponseEntity.ok(d);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // /app/debts and /topic/debts endpoint for web sockets
    @MessageMapping("/debts")
    @SendTo("/debts")
    public Debt updatePaidWebSocket(Long debtId, Boolean paid) {
        return null;
    }

}
