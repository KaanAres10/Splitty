package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import server.service.DebtService;
import server.service.EventService;
import server.service.ExpenseService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith (MockitoExtension.class)
public class ExpenseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DebtService debtService;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private WebSocketUtil webSocketUtil;

    @InjectMocks
    private ExpenseController expenseController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = standaloneSetup(expenseController).build();
    }

    @Test
    public void addEmptyExpenseTest() throws Exception {
        Expense expense = new Expense();
        Event event = new Event();
        event.setId(1L);
        expense.setEvent(event);

        mockMvc.perform(post("/api/events/{eventId}/expenses/create", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void addExpenseTest() throws Exception {
        Expense expense =
            new Expense(null, "test", new BigDecimal(123), new Participant(), new Date(), "euro",
                new ArrayList<>(), new ArrayList<>());
        Event event = new Event();
        event.setId(1L);
        expense.setEvent(event);

        when(expenseService.addExpense(any(Expense.class))).thenReturn(expense);

        mockMvc.perform(post("/api/events/{eventId}/expenses/create", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
            .andExpect(status().isOk());

    }

    @Test
    public void getByIdTest() throws Exception {
        Expense expense =
            new Expense(null, "test", new BigDecimal(123), new Participant(), new Date(), "euro",
                new ArrayList<>(), new ArrayList<>());
        Event event = new Event();
        event.setId(1L);
        expense.setEvent(event);
        expense.setId(1L);

        when(expenseService.getById(expense.getId())).thenReturn(expense);

        mockMvc.perform(get("/api/events/{eventId}/expenses/{expenseId}", 1L, 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("test"));
    }

    @Test
    public void getAllTest() throws Exception {
        List<Expense> list = new ArrayList<>();
        Event event = new Event();
        event.setId(1L);

        when(expenseService.getAll(event.getId())).thenReturn(list);

        mockMvc.perform(get("/api/events/{eventId}/expenses", 1L))
            .andExpect(status().isOk());
    }

    @Test
    public void addExpenseNoDebtsTest() throws Exception {
        Expense expense =
            new Expense(null, "test", new BigDecimal(123), new Participant(), new Date(), "euro",
                new ArrayList<>(), new ArrayList<>());
        Event event = new Event();
        event.setId(1L);
        expense.setEvent(event);

        when(expenseService.addExpense(any(Expense.class))).thenReturn(expense);

        mockMvc.perform(post("/api/events/{eventId}/expenses/create/nodebts", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
            .andExpect(status().isOk());

    }

    @Test
    public void addEmptyExpenseNoDebtsTest() throws Exception {
        Expense expense = new Expense();
        Event event = new Event();
        event.setId(1L);
        expense.setEvent(event);

        mockMvc.perform(post("/api/events/{eventId}/expenses/create/nodebts", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteExpense() {
        Expense expense = mock(Expense.class);
        Event event = mock(Event.class);
        expense.setId(1L);
        event.setId(1L);
        when(expenseService.getById(anyLong())).thenReturn(expense);
        when(expense.getEvent()).thenReturn(event);
        ResponseEntity<Expense> response = expenseController.deleteExpense(1L);
        assertEquals(200, response.getStatusCodeValue()); // Expecting OK
    }

    @Test
    public void editEmptyExpenseTest() throws Exception {
        Expense expense = new Expense();
        Event event = new Event();
        event.setId(1L);
        expense.setEvent(event);
        long expenseId = 1L;
        expense.setId(expenseId);

        when(expenseService.getById(expenseId)).thenReturn(expense);

        mockMvc.perform(put("/api/events/{eventId}/expenses/edit", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void editNoIdExpenseTest() throws Exception {
        Expense expense = new Expense();
        Event event = new Event();
        event.setId(1L);
        expense.setEvent(event);
        expense.setId(2L);

        mockMvc.perform(put("/api/events/{eventId}/expenses/edit", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void editExpenseTest() throws Exception {
        Expense expense =
            new Expense(null, "test", new BigDecimal(123), new Participant(), new Date(), "euro",
                new ArrayList<>(), new ArrayList<>());
        Event event = new Event();
        event.setId(1L);
        expense.setEvent(event);
        long expenseId = 1L;
        expense.setId(expenseId);

        when(expenseService.getById(expenseId)).thenReturn(expense);
        when(expenseService.updateExpenseDetails(any(Expense.class))).thenReturn(expense);

        mockMvc.perform(put("/api/events/{eventId}/expenses/edit", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense)))
            .andExpect(status().isOk());
    }

    @Test
    public void testGetAllExpensesByEventIdEventFound() {
        long eventId = 1L;
        List<Expense> expenses = new ArrayList<>();
        expenses.add(new Expense());
        Event event = new Event();
        event.setId(eventId);

        when(eventService.findById(eventId)).thenReturn(event);
        when(eventService.getAllExpensesByEventId(eventId)).thenReturn(expenses);

        ResponseEntity<List<Expense>> response = expenseController.getAllExpensesByEventId(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expenses, response.getBody());
    }

    @Test
    public void testGetAllExpensesByEventIdEventNotFound() {
        long eventId = 1L;

        when(eventService.findById(eventId)).thenReturn(null);

        ResponseEntity<List<Expense>> response = expenseController.getAllExpensesByEventId(eventId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }
}
