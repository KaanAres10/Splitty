package server.service;

import commons.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.TransferRepository;


@Service
public class TransferService {

    @Autowired
    private TransferRepository transferRepository;

    public Transfer transferById(Long id){
        return transferRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));
    }

    public Transfer createTransfer(Transfer transfer){
        return transferRepository
                .save(transfer);
    }
//    public List<Transfer> getAllTransferByEventId(Long eventId, Long debtId) {
//        return transferRepository.findByEventId(eventId, debtId);
//    }
}
