package server.service;

import commons.Transfer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.TransferRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    public void testTransferById() {
        Transfer transfer = new Transfer();
        transfer.setId(1L);

        when(transferRepository.findById(1L)).thenReturn(Optional.of(transfer));

        Transfer result = transferService.transferById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testTransferByIdNotFound() {
        when(transferRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transferService.transferById(1L));
    }

    @Test
    public void testCreateTransfer() {
        Transfer transfer = new Transfer();
        when(transferRepository.save(transfer)).thenReturn(transfer);

        Transfer result = transferService.createTransfer(transfer);

        assertNotNull(result);
        assertEquals(transfer, result);
    }
}
