package client.scenes;

import client.MyFXML;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import commons.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;


import java.util.Locale;

import static com.google.inject.Guice.createInjector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class EventOverviewCtrlTest extends ApplicationTest {
    private EventOverviewCtrl eventOverviewCtrl;

    private static ServerUtils serverMock;
    private static User userMock;
    private static Event eventMock;
    private static MainCtrl mainCtrlMock;

    @BeforeAll
    public static void setUp() throws Exception {
        System.setProperty("testfx.robot.move_max_count", "1");
        System.setProperty("testfx.robot.write_sleep", "1");
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
        serverMock = mock(ServerUtils.class);
        mainCtrlMock = mock(MainCtrl.class);
        eventMock = mock(Event.class);
    }

    @Test
    void testGetChoice(){
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().setAll(FXCollections.observableArrayList("Yoroda", "Eve"));
        comboBox.setValue("Yoroda");

        Platform.runLater(() -> {
            LanguageManager.getInstance().setCurrentLocale(new Locale("en"));
            eventOverviewCtrl.getChoice(comboBox);
            assertEquals("From Yoroda",eventOverviewCtrl.getFromPersonBtn().getText());
            assertEquals("Including Yoroda",eventOverviewCtrl.getIncludingPersonBtn().getText());

            LanguageManager.getInstance().setCurrentLocale(new Locale("de"));
            eventOverviewCtrl.getChoice(comboBox);
            assertEquals("Von Yoroda",eventOverviewCtrl.getFromPersonBtn().getText());
            assertEquals("Einschließlich Yoroda",eventOverviewCtrl.getIncludingPersonBtn().getText());

            LanguageManager.getInstance().setCurrentLocale(new Locale("fr"));
            eventOverviewCtrl.getChoice(comboBox);
            assertEquals("De Yoroda",eventOverviewCtrl.getFromPersonBtn().getText());
            assertEquals("Incluant Yoroda",eventOverviewCtrl.getIncludingPersonBtn().getText());

            LanguageManager.getInstance().setCurrentLocale(new Locale("nl"));
            eventOverviewCtrl.getChoice(comboBox);
            assertEquals("Van Yoroda",eventOverviewCtrl.getFromPersonBtn().getText());
            assertEquals("Inclusief Yoroda",eventOverviewCtrl.getIncludingPersonBtn().getText());
        });
    }

    @Test
    void testShowStats(){
        LanguageManager.getInstance().setCurrentLocale(new Locale("en"));
        eventOverviewCtrl.mainCtrl = mainCtrlMock;
        eventOverviewCtrl.event = eventMock;
        eventOverviewCtrl.showStatistics();
        verify(mainCtrlMock).showStatistics(any(Event.class));
    }

    @Test
    void testStop(){
        LanguageManager.getInstance().setCurrentLocale(new Locale("en"));
        eventOverviewCtrl.server = serverMock;
        eventOverviewCtrl.stop();
        verify(serverMock).stop();
    }

    @Override
    public void start(Stage stage) {
        var eventOverview =
                new MyFXML(createInjector()).load(EventOverviewCtrl.class, "client", "scenes",
                        "EventOverview.fxml");
        eventOverviewCtrl = eventOverview.getKey();
        stage.setScene(new Scene(eventOverview.getValue()));
        stage.show();
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().setAll(FXCollections.observableArrayList("a", "b", "c"));
        comboBox.setValue("a");
        eventOverviewCtrl.getChoice(comboBox);

    }

    @Test
    public void gettersTable() {
        // Create a sample data for TableRow
        String date = "2024-04-10";
        String description = "Sample expense description";
        String participants = "Participant 1, Participant 2";
        TextFlow tags = new TextFlow();
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        // Create a TableRow object
        EventOverviewCtrl.TableRow tableRow = new EventOverviewCtrl.TableRow(date, description, participants, tags, editButton, deleteButton);
        tableRow.getEditButton();
        tableRow.getDate();
        tableRow.getTags();
        tableRow.getParticipants();
        tableRow.getDescription();
        tableRow.setDeleteButton(new Button("Delete"));
        tableRow.setDate("2024-05-10");
        tableRow.setDescription("Test");
        tableRow.setParticipants("Participant 1");
        tableRow.setTags(new TextFlow());
        assertEquals("Delete", tableRow.getDeleteButton().getText());
        assertEquals("2024-05-10", tableRow.getDate());
        assertEquals("Test", tableRow.getDescription());
        assertEquals("Participant 1", tableRow.getParticipants().toString());

    }
    @Test
    public void testInitializeShortcuts() {
        assertTrue(eventOverviewCtrl.getSendInvite().isFocused());
        interact(() -> push(KeyCode.DOWN));
        assertTrue(eventOverviewCtrl.getFromPersonBtn().isFocused());
        interact(() -> push(KeyCode.DOWN));
        assertTrue(eventOverviewCtrl.getExpenseTable().isFocused());
        interact(() -> push(KeyCode.DOWN));
        assertTrue(eventOverviewCtrl.getSettleDebts().isFocused());
        interact(() -> push(KeyCode.UP));
        assertTrue(eventOverviewCtrl.getExpenseTable().isFocused());
        interact(() -> push(KeyCode.UP));
        assertTrue(eventOverviewCtrl.getIncludingPersonBtn().isFocused());
        interact(() -> push(KeyCode.DOWN));
        interact(() -> push(KeyCode.DOWN));
        interact(() -> push(KeyCode.LEFT));
        assertTrue(eventOverviewCtrl.getBackBtn().isFocused());
        interact(() -> push(KeyCode.DOWN));
        assertTrue(eventOverviewCtrl.getChangeTitleBtn().isFocused());
        interact(() -> push(KeyCode.DOWN));
        assertTrue(eventOverviewCtrl.getParticipantAdd().isFocused());
        interact(() -> push(KeyCode.UP));
        assertTrue(eventOverviewCtrl.getChangeTitleBtn().isFocused());
        interact(() -> push(KeyCode.DOWN));
        interact(() -> push(KeyCode.DOWN));
        assertTrue(eventOverviewCtrl.getAllTable().isFocused());
    }
    @Test
    public void testGetters() {
        eventOverviewCtrl.getRoot();
        eventOverviewCtrl.getSendInvite();
        eventOverviewCtrl.getFromPersonBtn();
        eventOverviewCtrl.getExpenseTable();
        eventOverviewCtrl.getSettleDebts();
        eventOverviewCtrl.getIncludingPersonBtn();
        eventOverviewCtrl.getParticipantsList();
        eventOverviewCtrl.getBackBtn();
        eventOverviewCtrl.getChangeTitleBtn();
        eventOverviewCtrl.getParticipantAdd();
        eventOverviewCtrl.getAllTable();
        eventOverviewCtrl.getAddExpense();
    }



}
