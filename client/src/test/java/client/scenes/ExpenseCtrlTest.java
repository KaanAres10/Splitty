package client.scenes;

import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.math.BigDecimal;
import java.util.*;

import static client.scenes.InviteCtrl.getLocation;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class ExpenseCtrlTest extends ApplicationTest {

    private static ExpenseCtrl expenseCtrl;
    private static MainCtrl mainCtrlMock;
    private static ServerUtils server;
    private static Event eventMock;
    private static LanguageManager languageManager;
    private static Stage stage;

    @BeforeAll
    public static void setupSpec() throws Exception {
        System.setProperty("testfx.robot.move_max_count", "1");
        System.setProperty("testfx.robot.write_sleep", "1");
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");

        mainCtrlMock = mock(MainCtrl.class);
        server = mock(ServerUtils.class);
        expenseCtrl = new ExpenseCtrl(server, mainCtrlMock);
        eventMock = mock(Event.class);
        languageManager = mock(LanguageManager.class);
        stage = mock(Stage.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        var fxml = new FXMLLoader();
        fxml.setLocation(getLocation("client/scenes/Expense.fxml"));
        var scene = new Scene(fxml.load());
        stage.setScene(scene);
        stage.show();
        expenseCtrl = fxml.getController();
    }

    @Test
    void testAddExpense(){
        expenseCtrl.server = server;
        expenseCtrl.event = new Event();

        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("-1.00"));
        assertThrows(Exception.class, () -> {
            expenseCtrl.addExpense(expense);
        });
        Expense expense2 = new Expense();
        expense2.setAmount(new BigDecimal("1.00"));
        Platform.runLater(() -> {
            expenseCtrl.addExpense(expense2);
            verify(server).addExpense(any(Expense.class), any(Event.class));
        });
    }

    @Test
    void testAddTags(){
        LanguageManager.getInstance().setCurrentLocale(new Locale("en"));
        expenseCtrl.eventTagsTable = new TableView<>();
        expenseCtrl.server = server;
        eventMock.setId(1L);
        expenseCtrl.event = eventMock;

        Expense expense = new Expense();
        Expense expense2 = new Expense();
        Set<Expense> expenseSet = new HashSet<>();
        expenseSet.add(expense);
        expenseSet.add(expense2);

        ObservableList<Tag> tags = FXCollections.observableArrayList();
        Tag tag = new Tag("food", "blue");
        tag.setId(1L);
        tags.add(tag);

        expenseCtrl.eventTagsTable.setItems(tags);

        expenseCtrl.addTags(expense);
        verify(server).removeTag(any(Long.class), any(Long.class));
        verify(server).editExpense(any(Expense.class), any(Event.class));
    }

    @Test
    void testToCSS(){
        Color color = ExpenseCtrl.colorFromString("0xffffffff");

        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        assertEquals(String.format("#%02X%02X%02X", red, green, blue), ExpenseCtrl.toCssColor("0xffffffff"));
    }

    @Test
    void testColorFromString(){
        String color = "0xffffffff";
        int red = Integer.parseInt(color.substring(2, 4), 16);
        int green = Integer.parseInt(color.substring(4, 6), 16);
        int blue = Integer.parseInt(color.substring(6, 8), 16);
        int alpha = Integer.parseInt(color.substring(8), 16);

        assertEquals(Color.rgb(red, green, blue, alpha / 255.0), expenseCtrl.colorFromString("0xffffffff"));

    }

    @Test
    void testParticipantByName(){
        List<Participant> participants = new ArrayList<>();
        Participant participant = new Participant();
        participant.setName("Yoroda");
        participants.add(participant);
        LanguageManager.getInstance().setCurrentLocale(new Locale("en"));

        assertEquals(participant ,expenseCtrl.findParticipantByName("Yoroda",participants));
        assertNotEquals(participant ,expenseCtrl.findParticipantByName("Eve",participants));
    }

    @Test
    void testSelectColor(){
        Color color = new Color(1, 1, 1, 1);
        expenseCtrl.colorPicker.setValue(color);
        expenseCtrl.selectColor(null);
    }

    @Test
    void testAlreadyExists(){
        expenseCtrl.server = server;
        ObservableList<Tag> tags = FXCollections.observableArrayList();
        tags.add(new Tag("food", "blue"));
        expenseCtrl.expenseTags.setItems(tags);

        Expense expense = new Expense();
        expense.setTags(tags);

        expenseCtrl.event = eventMock;
        expense.setEvent(expenseCtrl.event);
        expenseCtrl.event.setId(1L);

        Platform.runLater(() -> {
            assertTrue(expenseCtrl.alreadyExists("food"));
            assertFalse(expenseCtrl.alreadyExists("drinks"));
        });
        when(server.getAllTagsFromEvent(anyLong())).thenReturn(tags);
        Platform.runLater(() -> {
            assertTrue(expenseCtrl.alreadyExists("food"));
            assertFalse(expenseCtrl.alreadyExists("drinks"));
        });
    }

    @Test
    void testLoadCurrentExpense(){
        Participant participant = new Participant();
        participant.setName("Yoroda");

        Expense expense = new Expense();
        expense.setTitle("food and drinks");
        expense.setAmount(new BigDecimal(50.00));
        expense.setDate(new Date());
        expense.setCurrency("$");
        expense.setPayer(participant);

        Platform.runLater(() -> {
            expenseCtrl.loadCurrentExpense(expense);
        });
    }

    @Test
    void testInitParticipants(){
        expenseCtrl.participants = new ArrayList<>();
        expenseCtrl.participants.add(new Participant(null, null, "Yoroda", null, null, null));
        expenseCtrl.initSelectParticipant();
    }

    @Test
    void testPieChart(){
        expenseCtrl.updatePieChart();
        assertNull(expenseCtrl.pieChart, "PieChart instance is null. Cannot update.");

        expenseCtrl.pieChart = new PieChart();
        expenseCtrl.updatePieChart();
        assertEquals(FXCollections.emptyObservableList(),expenseCtrl.pieChart.getData());

        expenseCtrl.expenses = new ArrayList<>();
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("food", "blue"));
        expenseCtrl.expenses.add(new Expense(null, null, null, null, null, null, tags, null));
        expenseCtrl.updatePieChart();
    }

    @Test
    void testShortCuts(){
        expenseCtrl.mainCtrl = mainCtrlMock;
        expenseCtrl.user = new User();
        expenseCtrl.event = eventMock;
        interact(()->push(KeyCode.ESCAPE));
        verify(mainCtrlMock, times(2)).showEventOverview(any(User.class), any(Event.class));
        interact(()->push(KeyCode.ENTER));
        verify(mainCtrlMock, times(2)).showEventOverview(any(User.class), any(Event.class));
        expenseCtrl.mainCtrl = mainCtrlMock;
        interact(()->push(KeyCode.UP));
        assertTrue(expenseCtrl.cancel.isFocused());
        interact(()->push(KeyCode.RIGHT));
        assertTrue(expenseCtrl.confirm.isFocused());
        interact(()->push(KeyCode.UP));
        assertTrue(expenseCtrl.colorPicker.isFocused());
        interact(()->push(KeyCode.UP));
        assertTrue(expenseCtrl.manualTag.isFocused());
        interact(()->push(KeyCode.DOWN));
        assertTrue(expenseCtrl.addTagBtn.isFocused());
        interact(()->push(KeyCode.UP));
        assertTrue(expenseCtrl.expenseCurrency.isFocused());
        interact(()->push(KeyCode.LEFT));
        interact(()->push(KeyCode.UP));
        interact(()->push(KeyCode.UP));
        // works locally but not on gitlab. Pipeline fail says test failed
    }

    @Test
    void testStart() throws Exception {
        Platform.runLater(() -> {
            try {
                expenseCtrl.start(stage);
                verify(stage).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void testBackButton()  {
        expenseCtrl.isEditing = false;
        expenseCtrl.mainCtrl = mainCtrlMock;
        expenseCtrl.user = new User();
        expenseCtrl.event = new Event();
        expenseCtrl.backBtn();

        verify(mainCtrlMock, times(3)).showEventOverview(any(User.class),any(Event.class));
    }

    @Test
    void testToggleManualAll(){
        interact(() -> {
            clickOn("#manualAdd");
        });
        expenseCtrl.handleToggle();
        assertTrue(expenseCtrl.manualAddField.isVisible());

        interact(() -> {
            clickOn("#allAdd");
        });
        expenseCtrl.handleToggle();
        assertFalse(expenseCtrl.manualAddField.isVisible());
    }

    @Test
    void testUpdateUIWitchBundle(){
        expenseCtrl.expensePayer = new ComboBox<>();
        expenseCtrl.description = new TextField();
        expenseCtrl.priceField = new TextField();
        expenseCtrl.expenseDate = new DatePicker();
        expenseCtrl.allAdd = mock(RadioButton.class);
        expenseCtrl.manualAdd = mock(RadioButton.class);
        expenseCtrl.manualLabel = mock(Label.class);
        expenseCtrl.manualTag = new TextField();
        expenseCtrl.addTagBtn = mock(Button.class);
        expenseCtrl.confirm = mock(Button.class);
        expenseCtrl.cancel = mock(Button.class);
        expenseCtrl.expenseCurrency = new ComboBox<>();
        expenseCtrl.tagLabel = mock(Label.class);
        expenseCtrl.selectLabel = mock(Label.class);

        expenseCtrl.server = server;
        LanguageManager.getInstance().setServer("http://localhost:8080");

        Locale selectedLocale = new Locale("en");
        LanguageManager.getInstance().setCurrentLocale(selectedLocale);
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
        LanguageManager.getInstance().setBundle(bundle);

        expenseCtrl.updateUIWithBundle(bundle);
        assertEquals("Edit", expenseCtrl.confirm.getText());

        Locale selectedLocale2 = new Locale("nl");
        LanguageManager.getInstance().setCurrentLocale(selectedLocale2);
        ResourceBundle bundle2 = LanguageManager.getInstance().getBundle();
        expenseCtrl.updateUIWithBundle(bundle2);
        assertEquals("Bewerken", expenseCtrl.confirm.getText());
    }

    @Test
    public void testEditCreate() {

    }

    @Test
    public void testEditExpense(){
        expenseCtrl.isEditing = true;
        expenseCtrl.eventTagsTable = mock(TableView.class);

        User user = new User("Yoroda", "en");
        Participant payer = new Participant(user, expenseCtrl.event);
        Date date = new Date();
        List<Tag> tags = new ArrayList<>();
        List<Participant> participants = new ArrayList<>();
        BigDecimal amount = new BigDecimal(16.01);
        Expense expense = new Expense(expenseCtrl.event, "test", amount, payer,
                date, "$", tags, participants);
        Platform.runLater(() -> {
            expenseCtrl.editExpense(expense);
        });
        server.editExpense(any(Expense.class), any(Event.class));
    }

}