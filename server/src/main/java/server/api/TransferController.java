package server.api;

import commons.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.TransferService;


@RestController
@RequestMapping ("/api/events/{eventId}/{debtId}/transfers")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @PostMapping ("/create")
    public ResponseEntity<Transfer> createTransfer(@RequestBody Transfer transfer) {
        if (transfer == null || transfer.getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Transfer okTransfer = transferService
            .createTransfer(transfer);
        return new ResponseEntity<>(okTransfer, HttpStatus.CREATED);
    }

    @PostMapping ("/{id}")
    public ResponseEntity<Transfer> changeApproval(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        Transfer toFind = transferService.transferById(id);
        if (!toFind.isApproved()) {
            toFind.setApproved(true);
        } else if (toFind.isApproved()) {
            toFind.setApproved(false);
        }
        return ResponseEntity.ok(toFind);
    }
}
