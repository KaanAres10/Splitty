package client.scenes;

import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminCurrentEventsCtrlTest extends ApplicationTest {

    private static AdminCurrentEventsCtrl adminCurrentEventsCtrl;
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
        adminCurrentEventsCtrl = new AdminCurrentEventsCtrl(server, mainCtrlMock);
        eventMock = mock(Event.class);
        languageManager = mock(LanguageManager.class);
        stage = mock(Stage.class);
    }

    @Test
    void testDeleteEN(){
        Event event = new Event();
        Platform.runLater(() -> {
            LanguageManager.getInstance().setCurrentLocale(new Locale("en"));
            adminCurrentEventsCtrl.deleteButtonPressed(event);
            assertEquals("Confirmation Dialog", adminCurrentEventsCtrl.alertConfirmation.getTitle());
            assertEquals("Delete Confirmation", adminCurrentEventsCtrl.alertConfirmation.getHeaderText());
            assertEquals("Are you sure you want to delete the Event?", adminCurrentEventsCtrl.alertConfirmation.getContentText());
            assertEquals("Yes", adminCurrentEventsCtrl.alertConfirmation.getButtonTypes().getFirst().getText());
            assertEquals("No", adminCurrentEventsCtrl.alertConfirmation.getButtonTypes().get(1).getText());
        });
    }

    @Test
    void testDeleteDE(){
        Event event = new Event();
        Platform.runLater(() -> {
            LanguageManager.getInstance().setCurrentLocale(new Locale("de"));
            adminCurrentEventsCtrl.deleteButtonPressed(event);
            assertEquals("Bestätigungsdialog", adminCurrentEventsCtrl.alertConfirmation.getTitle());
            assertEquals("Löschbestätigung", adminCurrentEventsCtrl.alertConfirmation.getHeaderText());
            assertEquals("Möchten Sie das Ereignis wirklich löschen?", adminCurrentEventsCtrl.alertConfirmation.getContentText());
            assertEquals("Ja", adminCurrentEventsCtrl.alertConfirmation.getButtonTypes().getFirst().getText());
            assertEquals("Nein", adminCurrentEventsCtrl.alertConfirmation.getButtonTypes().get(1).getText());
        });
    }

    @Test
    void testDeleteFR(){
        Event event = new Event();
        Platform.runLater(() -> {
            LanguageManager.getInstance().setCurrentLocale(new Locale("fr"));
            adminCurrentEventsCtrl.deleteButtonPressed(event);
            assertEquals("Boîte de dialogue de confirmation", adminCurrentEventsCtrl.alertConfirmation.getTitle());
            assertEquals("Confirmation de suppression", adminCurrentEventsCtrl.alertConfirmation.getHeaderText());
            assertEquals("Êtes-vous sûr de vouloir supprimer l'événement ?", adminCurrentEventsCtrl.alertConfirmation.getContentText());
            assertEquals("Oui", adminCurrentEventsCtrl.alertConfirmation.getButtonTypes().getFirst().getText());
            assertEquals("Non", adminCurrentEventsCtrl.alertConfirmation.getButtonTypes().get(1).getText());
        });
    }

    @Test
    void testDeleteNL(){
        Event event = new Event();
        Platform.runLater(() -> {
            LanguageManager.getInstance().setCurrentLocale(new Locale("nl"));
            adminCurrentEventsCtrl.deleteButtonPressed(event);
            assertEquals("Bevestigingsvenster", adminCurrentEventsCtrl.alertConfirmation.getTitle());
            assertEquals("Verwijderingsbevestiging", adminCurrentEventsCtrl.alertConfirmation.getHeaderText());
            assertEquals("Weet u zeker dat u het evenement wilt verwijderen?", adminCurrentEventsCtrl.alertConfirmation.getContentText());
            assertEquals("Ja", adminCurrentEventsCtrl.alertConfirmation.getButtonTypes().getFirst().getText());
            assertEquals("Nee", adminCurrentEventsCtrl.alertConfirmation.getButtonTypes().get(1).getText());
        });
    }

    @Test
    void testLeaveAdminEN(){
        Platform.runLater(() -> {
            LanguageManager.getInstance().setCurrentLocale(new Locale("en"));
            adminCurrentEventsCtrl.leaveAdminIsPressed();
            assertEquals("Confirmation Dialog", adminCurrentEventsCtrl.alertConfirmation.getTitle());
            assertEquals("Admin Mode", adminCurrentEventsCtrl.alertConfirmation.getHeaderText());
            assertEquals("Are you sure you want to leave the Admin Mode?", adminCurrentEventsCtrl.alertConfirmation.getContentText());
            assertEquals("Yes", adminCurrentEventsCtrl.alertConfirmation.getButtonTypes().getFirst().getText());
            assertEquals("No", adminCurrentEventsCtrl.alertConfirmation.getButtonTypes().get(1).getText());
        });
    }

    @Test
    void testLeaveAdminNL() {
        Platform.runLater(() -> {
            LanguageManager.getInstance().setCurrentLocale(new Locale("nl"));
            adminCurrentEventsCtrl.leaveAdminIsPressed();
            assertEquals("Bevestigingsvenster", adminCurrentEventsCtrl.alertConfirmation.getTitle());
            assertEquals("Beheerdersmodus", adminCurrentEventsCtrl.alertConfirmation.getHeaderText());
            assertEquals("Weet u zeker dat u de beheerdersmodus wilt verlaten?", adminCurrentEventsCtrl.alertConfirmation.getContentText());
            assertEquals("Ja", adminCurrentEventsCtrl.alertConfirmation.getButtonTypes().getFirst().getText());
            assertEquals("Nee", adminCurrentEventsCtrl.alertConfirmation.getButtonTypes().get(1).getText());
        });
    }

    @Test
    void testLeaveAdminFR() {
        Platform.runLater(() -> {
            LanguageManager.getInstance().setCurrentLocale(new Locale("fr"));
            adminCurrentEventsCtrl.leaveAdminIsPressed();
            assertEquals("Boîte de dialogue de confirmation", adminCurrentEventsCtrl.alertConfirmation.getTitle());
            assertEquals("Mode administrateur", adminCurrentEventsCtrl.alertConfirmation.getHeaderText());
            assertEquals("Êtes-vous sûr de vouloir quitter le mode administrateur ?", adminCurrentEventsCtrl.alertConfirmation.getContentText());
            assertEquals("Oui", adminCurrentEventsCtrl.alertConfirmation.getButtonTypes().getFirst().getText());
            assertEquals("Non", adminCurrentEventsCtrl.alertConfirmation.getButtonTypes().get(1).getText());
        });
    }

    @Test
    void testLeaveAdminDE() {
        Platform.runLater(() -> {
            LanguageManager.getInstance().setCurrentLocale(new Locale("de"));
            adminCurrentEventsCtrl.leaveAdminIsPressed();
            assertEquals("Bestätigungsdialog", adminCurrentEventsCtrl.alertConfirmation.getTitle());
            assertEquals("Admin-Modus", adminCurrentEventsCtrl.alertConfirmation.getHeaderText());
            assertEquals("Sind Sie sicher, dass Sie den Admin-Modus verlassen möchten?", adminCurrentEventsCtrl.alertConfirmation.getContentText());
            assertEquals("Ja", adminCurrentEventsCtrl.alertConfirmation.getButtonTypes().getFirst().getText());
            assertEquals("Nein", adminCurrentEventsCtrl.alertConfirmation.getButtonTypes().get(1).getText());
        });
    }

    @Test
    void testRefresh(){
        adminCurrentEventsCtrl.tableEvents = new TableView<>();
        adminCurrentEventsCtrl.noEventsLabel = new Label();
        adminCurrentEventsCtrl.refresh();
        assertTrue(adminCurrentEventsCtrl.noEventsLabel.isVisible());
        assertFalse(adminCurrentEventsCtrl.tableEvents.isVisible());

        List<Event> eventList = new ArrayList<>();
        eventList.add(new Event());
        when(adminCurrentEventsCtrl.server.getAllEvents()).thenReturn(eventList);
        adminCurrentEventsCtrl.refresh();
        assertFalse(adminCurrentEventsCtrl.noEventsLabel.isVisible());
        assertTrue(adminCurrentEventsCtrl.tableEvents.isVisible());

        adminCurrentEventsCtrl.downloadButtonColumn = new TableColumn<>();
        adminCurrentEventsCtrl.deleteButtonColumn = new TableColumn<>();
        adminCurrentEventsCtrl.refresh();

        LanguageManager.getInstance().setCurrentLocale(new Locale("en"));
        ResourceBundle bundle = ResourceBundle.getBundle("labels", new Locale("en"));
        adminCurrentEventsCtrl.refresh();

        LanguageManager.getInstance().setCurrentLocale(new Locale("nl"));
        ResourceBundle bundle2 = ResourceBundle.getBundle("labels", new Locale("nl"));
        adminCurrentEventsCtrl.refresh();

        LanguageManager.getInstance().setCurrentLocale(new Locale("fr"));
        ResourceBundle bundle3 = ResourceBundle.getBundle("labels", new Locale("fr"));
        adminCurrentEventsCtrl.refresh();

        LanguageManager.getInstance().setCurrentLocale(new Locale("de"));
        ResourceBundle bundle4 = ResourceBundle.getBundle("labels", new Locale("de"));
        adminCurrentEventsCtrl.refresh();
    }

    @Test
    void testUpdateUIWitchBundle(){
        adminCurrentEventsCtrl.eventsLabel = mock(Label.class);
        adminCurrentEventsCtrl.eventsLabel1 = mock(Label.class);
        adminCurrentEventsCtrl.noEventsLabel = mock(Label.class);
        adminCurrentEventsCtrl.tableTitleColumn = new TableColumn<>();
        adminCurrentEventsCtrl.tableCreationDateColumn = new TableColumn<>();
        adminCurrentEventsCtrl.tableLastActivityColumn = new TableColumn<>();
        adminCurrentEventsCtrl.tableInviteCodeColumn = new TableColumn<>();
        adminCurrentEventsCtrl.leaveAdmin = mock(Button.class);
        adminCurrentEventsCtrl.eventsLabel1 = mock(Label.class);
        adminCurrentEventsCtrl.importEvent = mock(Button.class);

        LanguageManager.getInstance().setServer("http://localhost:8080");

        Locale selectedLocale = new Locale("en");
        LanguageManager.getInstance().setCurrentLocale(selectedLocale);
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
        LanguageManager.getInstance().setBundle(bundle);

        adminCurrentEventsCtrl.updateUIWithBundle(bundle);
        assertEquals("Import event", adminCurrentEventsCtrl.importEvent.getText());

        Locale selectedLocale2 = new Locale("nl");
        LanguageManager.getInstance().setCurrentLocale(selectedLocale2);
        ResourceBundle bundle2 = LanguageManager.getInstance().getBundle();
        adminCurrentEventsCtrl.updateUIWithBundle(bundle2);
        assertEquals("Evenement importeren", adminCurrentEventsCtrl.importEvent.getText());
    }
}