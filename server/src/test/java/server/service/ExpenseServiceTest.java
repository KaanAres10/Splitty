package server.service;

import commons.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.EventLastActivityListener;
import server.database.DebtRepository;
import server.database.ExpenseRepository;
import server.database.TagRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private DebtRepository debtRepository;
    @Mock
    private TagRepository tagRepository;

    @Mock
    private EventLastActivityListener eventLastActivityListener;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    public void createExpense(){
        Expense expense = mock(Expense.class);
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        Expense result = expenseService.addExpense(expense);
        assertNotNull(result);
        assertEquals(result, expense);
    }

    @Test
    void getByIdExist() {
        long expenseId = 1L;
        Expense mockedExpense = new Expense();

        when(expenseRepository.findById(expenseId)).
                thenReturn(Optional.of(mockedExpense));

        Expense resultExpense = expenseService.getById(expenseId);

        // Assert the result
        assertEquals(mockedExpense, resultExpense);
    }

    @Test
    void addExpenseTest() {
        Expense expenseToAdd = new Expense();

        // Mock
        when(expenseRepository.save(expenseToAdd)).thenReturn(expenseToAdd);

        // Service method
        Expense addedExpense = expenseService.addExpense(expenseToAdd);

        // Assert
        assertEquals(expenseToAdd, addedExpense);
    }

    @Test
    void deleteExpenseTest() {
        Expense expense = new Expense();
        long expenseId = 1L;
        expense.setId(expenseId);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        doNothing().when(expenseRepository).delete(expense);

        boolean isDeleted = expenseService.deleteExpense(expense);
        assertTrue(isDeleted);
        boolean isNotDeleted = expenseService.deleteExpense(new Expense());
        assertFalse(isNotDeleted);
        // Verify
        verify(expenseRepository, times(1)).delete(expense);
    }

    @Test
    void updateExpenseTest() {
        long expenseId = 1L;
        String newTitle = "NewTitle";
        BigDecimal newAmount = BigDecimal.valueOf(100.00);
        Participant newParticipant = new Participant();
        newParticipant.setId(1L);

        Expense currentExpense = new Expense();
        currentExpense.setId(expenseId);
        currentExpense.setTitle("OldTitle");
        currentExpense.setAmount(BigDecimal.valueOf(50.00));
        currentExpense.setPayer(new Participant());

        Participant debtor1 = new Participant();
        Participant debtor2 = new Participant();
        debtor1.setId(2L);
        debtor2.setId(3L);
        Debt debt1 = new Debt(
                currentExpense,
                debtor1,
                BigDecimal.valueOf(25.00),
                false,
                currentExpense.getPayer(),
                null
        );
        Debt debt2 = new Debt(
                currentExpense,
                debtor2,
                BigDecimal.valueOf(25.00),
                false,
                currentExpense.getPayer(),
                null
        );

        // Mock
        when(expenseRepository.findById(expenseId)).
                thenReturn(Optional.of(currentExpense));

        // Mock
        when(expenseRepository.save(any(Expense.class))).
                thenAnswer(invocation -> invocation.getArgument(0));

        when(debtRepository.save(any(Debt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(debtRepository.getDebtsByExpense(currentExpense))
                .thenReturn(List.of(debt1, debt2));

        currentExpense.setAmount(newAmount);
        currentExpense.setTitle(newTitle);
        currentExpense.setPayer(newParticipant);
        currentExpense.setParticipants(List.of(debtor1, debtor2));

        // Service method
        Expense updatedExpense = expenseService.
                updateExpenseDetails(currentExpense);

        // Verify
        verify(expenseRepository, times(1)).findById(expenseId);

        // Verify
        verify(expenseRepository, times(1)).save(eq(currentExpense));

        // Assert
        assertEquals(expenseId, updatedExpense.getId());
        assertEquals(newTitle, updatedExpense.getTitle());
        assertEquals(newAmount, updatedExpense.getAmount());
        assertEquals(newParticipant, updatedExpense.getPayer());
        assertEquals(BigDecimal.valueOf(50.00).doubleValue(), debt1.getAmount().doubleValue());
        assertEquals(BigDecimal.valueOf(50.00).doubleValue(), debt2.getAmount().doubleValue());
        assertEquals(newParticipant, debt1.getReceiver());
        assertEquals(newParticipant, debt2.getReceiver());
    }

    @Test
    public void getAllTest(){
        List<Expense> list = new ArrayList<>();
        long eventId = 1L;

        when(expenseRepository.getExpensesByEventId(eventId)).thenReturn(list);

        expenseService.getAll(eventId);

        assertNotNull(list);
    }

    @Test
    public void testUpdateExpenseDetailsTagsExist() {
        Expense expense = new Expense();
        expense.setId(1L);
        List<Tag> tags = new ArrayList<>();
        Tag tag = new Tag();
        tags.add(tag);
        expense.setTags(tags);
        expense.setParticipants(List.of(new Participant()));
        expense.setAmount(BigDecimal.ZERO);

        when(expenseRepository.findById(expense.getId())).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Expense updatedExpense = expenseService.updateExpenseDetails(expense);
        System.out.println(updatedExpense.getTags().size());
        verify(tagRepository, times(1)).save(any(Tag.class));
        assertEquals(expense, updatedExpense);
        assertTrue(tag.getExpenses().contains(expense));
    }
}