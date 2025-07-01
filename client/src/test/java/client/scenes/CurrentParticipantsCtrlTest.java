package client.scenes;

import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.User;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CurrentParticipantsCtrlTest extends ApplicationTest {
    private static CurrentParticipantsCtrl currentParticipantsCtrl;
    private static MainCtrl mainCtrlMock;
    private static ServerUtils server;
    private static LanguageManager languageManager;
    private static Stage stage;
    private static Event event;
    private static User user;

    @BeforeAll
    public static void setUpEntities(){
        mainCtrlMock = mock(MainCtrl.class);
        server = mock(ServerUtils.class);
        event = new Event("test");
        event.setId(1L);
        user = new User();
        user.setId(1L);
        currentParticipantsCtrl = new CurrentParticipantsCtrl(server, mainCtrlMock, event, user);
        languageManager = mock(LanguageManager.class);
        stage = mock(Stage.class);
    }

    @Test
    void testUpdateBundle(){
        currentParticipantsCtrl.eventNameLabel = mock(Label.class);
        currentParticipantsCtrl.backButton = mock(Button.class);
        currentParticipantsCtrl.addButton = mock(Button.class);
        currentParticipantsCtrl.tableNameColumn = new TableColumn<>();
        currentParticipantsCtrl.noLabel = mock(Label.class);

        LanguageManager.getInstance().setCurrentLocale(new Locale("en"));
        ResourceBundle bundle2 = LanguageManager.getInstance().getBundle();
        currentParticipantsCtrl.updateUIWithBundle(bundle2);
        assertEquals("Add", currentParticipantsCtrl.addButton.getText());

        LanguageManager.getInstance().setCurrentLocale(new Locale("nl"));
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
        currentParticipantsCtrl.updateUIWithBundle(bundle);
        assertEquals("Terug", currentParticipantsCtrl.backButton.getText());
    }

    @Test
    void testLocalizedText(){
        LanguageManager.getInstance().setCurrentLocale(new Locale("en"));
        assertEquals("Edit", currentParticipantsCtrl.getLocalizedText("Edit"));
        assertEquals("Delete", currentParticipantsCtrl.getLocalizedText("Delete"));
        assertEquals("", currentParticipantsCtrl.getLocalizedText("Hi"));

        LanguageManager.getInstance().setCurrentLocale(new Locale("de"));
        assertEquals("Bearbeiten", currentParticipantsCtrl.getLocalizedText("Edit"));
        assertEquals("Löschen", currentParticipantsCtrl.getLocalizedText("Delete"));
        assertEquals("", currentParticipantsCtrl.getLocalizedText("Hi"));

        LanguageManager.getInstance().setCurrentLocale(new Locale("fr"));
        assertEquals("Éditer", currentParticipantsCtrl.getLocalizedText("Edit"));
        assertEquals("Supprimer", currentParticipantsCtrl.getLocalizedText("Delete"));
        assertEquals("", currentParticipantsCtrl.getLocalizedText("Hi"));

        LanguageManager.getInstance().setCurrentLocale(new Locale("nl"));
        assertEquals("Bewerken", currentParticipantsCtrl.getLocalizedText("Edit"));
        assertEquals("Verwijderen", currentParticipantsCtrl.getLocalizedText("Delete"));
        assertEquals("", currentParticipantsCtrl.getLocalizedText("Hi"));

    }

    @Test
    void deleteButtonEN(){
        Platform.runLater(() -> {
            LanguageManager.getInstance().setCurrentLocale(new Locale("en"));
            currentParticipantsCtrl.deleteButtonPressed(new Participant());
            assertEquals("Confirmation Dialog", currentParticipantsCtrl.alertConfirmation.getTitle());
            assertEquals("Delete Confirmation", currentParticipantsCtrl.alertConfirmation.getHeaderText());
            assertEquals("Are you sure you want to delete the Participant?", currentParticipantsCtrl.alertConfirmation.getContentText());
            assertEquals("Yes", currentParticipantsCtrl.alertConfirmation.getButtonTypes().getFirst().getText());
            assertEquals("No", currentParticipantsCtrl.alertConfirmation.getButtonTypes().get(1).getText());
        });
    }

    @Test
    void deleteButtonDE(){
        Platform.runLater(() -> {
            LanguageManager.getInstance().setCurrentLocale(new Locale("de"));
            currentParticipantsCtrl.deleteButtonPressed(new Participant());
            assertEquals("Bestätigung", currentParticipantsCtrl.alertConfirmation.getTitle());
            assertEquals("Löschen Bestätigung", currentParticipantsCtrl.alertConfirmation.getHeaderText());
            assertEquals("Sind Sie sicher, dass Sie den Teilnehmer löschen möchten?", currentParticipantsCtrl.alertConfirmation.getContentText());
            assertEquals("Ja", currentParticipantsCtrl.alertConfirmation.getButtonTypes().getFirst().getText());
            assertEquals("Nein", currentParticipantsCtrl.alertConfirmation.getButtonTypes().get(1).getText());
        });
    }

    @Test
    void deleteButtonFR(){
        Platform.runLater(() -> {
            LanguageManager.getInstance().setCurrentLocale(new Locale("fr"));
            currentParticipantsCtrl.deleteButtonPressed(new Participant());
            assertEquals("Dialogue de Confirmation", currentParticipantsCtrl.alertConfirmation.getTitle());
            assertEquals("Confirmation de Suppression", currentParticipantsCtrl.alertConfirmation.getHeaderText());
            assertEquals("Êtes-vous sûr de vouloir supprimer le participant ?", currentParticipantsCtrl.alertConfirmation.getContentText());
            assertEquals("Oui", currentParticipantsCtrl.alertConfirmation.getButtonTypes().getFirst().getText());
            assertEquals("Non", currentParticipantsCtrl.alertConfirmation.getButtonTypes().get(1).getText());
        });
    }

    @Test
    void deleteButtonNL(){
        Platform.runLater(() -> {
            LanguageManager.getInstance().setCurrentLocale(new Locale("nl"));
            currentParticipantsCtrl.deleteButtonPressed(new Participant());
            assertEquals("Bevestigingsvenster", currentParticipantsCtrl.alertConfirmation.getTitle());
            assertEquals("Bevestiging verwijderen", currentParticipantsCtrl.alertConfirmation.getHeaderText());
            assertEquals("Weet u zeker dat u de deelnemer wilt verwijderen?", currentParticipantsCtrl.alertConfirmation.getContentText());
            assertEquals("Ja", currentParticipantsCtrl.alertConfirmation.getButtonTypes().getFirst().getText());
            assertEquals("Nee", currentParticipantsCtrl.alertConfirmation.getButtonTypes().get(1).getText());
        });
    }

    @Test
    void editButton(){
        currentParticipantsCtrl.editButtonPressed(new Participant());
        verify(mainCtrlMock).showContactDetails(any(Participant.class));
    }

    @Test
    void addButton(){
        currentParticipantsCtrl.addButtonPressed();
        verify(mainCtrlMock).showContactDetails(any(User.class), any(Event.class));
    }

    @Test
    void backButton(){
        currentParticipantsCtrl.backButtonPressed();
        verify(mainCtrlMock).showEventOverview(any(User.class), any(Event.class));
    }

    @Test
    public void testCanBeDeletedMethod(){
        Expense expense1 = new Expense();
        Expense expense2 = new Expense();
        Participant participant = new Participant();
        participant.setName("Leo");
        participant.setId(1L);
        expense2.setPayer(participant);
        when(server.getAllExpensesFromEvent(event.getId())).thenReturn(Arrays.asList(expense1, expense2));
        assertFalse(currentParticipantsCtrl.canBeDeleted(participant));

        expense2.setPayer(null);
        assertTrue(currentParticipantsCtrl.canBeDeleted(participant));

        expense2.setParticipants(new ArrayList<>());
        expense2.getParticipants().add(participant);
        assertFalse(currentParticipantsCtrl.canBeDeleted(participant));
    }
}
