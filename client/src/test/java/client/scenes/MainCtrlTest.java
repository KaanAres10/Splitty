package client.scenes;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ResourceBundle;

import static org.mockito.Mockito.*;

public class MainCtrlTest {

    private MainCtrl mainCtrlMock;

    @BeforeEach
    public void setUp() {
        mainCtrlMock = mock(MainCtrl.class);
    }

    @Test
    public void testShowStartScreen() {
        // When
        mainCtrlMock.showStartScreen();

        // Then
        verify(mainCtrlMock).showStartScreen();
    }

    @Test
    public void testShowStatistics() {
        // Given
        Event eventMock = mock(Event.class);

        // When
        mainCtrlMock.showStatistics(eventMock);

        // Then
        verify(mainCtrlMock).showStatistics(eventMock);
    }

    @Test
    public void testShowInvite() {
        // Given
        Event eventMock = mock(Event.class);

        // When
        mainCtrlMock.showInvite(eventMock);

        // Then
        verify(mainCtrlMock).showInvite(eventMock);
    }

    @Test
    public void testShowEventOverview() {
        // Given
        User userMock = mock(User.class);
        Event eventMock = mock(Event.class);

        // When
        mainCtrlMock.showEventOverview(userMock, eventMock);

        // Then
        verify(mainCtrlMock).showEventOverview(userMock, eventMock);
    }

    @Test
    public void testShowCurrentParticipants() {
        // Given
        User userMock = mock(User.class);
        Event eventMock = mock(Event.class);

        // When
        mainCtrlMock.showCurrentParticipants(userMock, eventMock);

        // Then
        verify(mainCtrlMock).showCurrentParticipants(userMock, eventMock);
    }

    @Test
    public void testShowContactDetails() {
        // Given
        User userMock = mock(User.class);
        Event eventMock = mock(Event.class);
        Participant participantMock = mock(Participant.class);

        // When
        mainCtrlMock.showContactDetails(userMock, eventMock);
        mainCtrlMock.showContactDetails(participantMock);
        mainCtrlMock.showContactDetails(userMock, eventMock, true);

        // Then
        verify(mainCtrlMock).showContactDetails(userMock, eventMock);
        verify(mainCtrlMock).showContactDetails(participantMock);
        verify(mainCtrlMock).showContactDetails(userMock, eventMock, true);
    }

    @Test
    public void testShowAddEdit() {
        // Given
        User userMock = mock(User.class);
        Event eventMock = mock(Event.class);
        Expense expenseMock = mock(Expense.class);

        // When
        mainCtrlMock.showAddEdit(userMock, eventMock, expenseMock);

        // Then
        verify(mainCtrlMock).showAddEdit(userMock, eventMock, expenseMock);
    }

    @Test
    public void testShowDebts() {
        // Given
        User userMock = mock(User.class);
        Event eventMock = mock(Event.class);

        // When
        mainCtrlMock.showDebts(userMock, eventMock);

        // Then
        verify(mainCtrlMock).showDebts(userMock, eventMock);
    }

    @Test
    public void testShowSettings() {
        // When
        mainCtrlMock.showSettings();

        // Then
        verify(mainCtrlMock).showSettings();
    }

    @Test
    public void testUpdateLanguage() {
        // Given
        ResourceBundle bundleMock = mock(ResourceBundle.class);

        // When
        mainCtrlMock.updateLanguage(bundleMock);

        // Then
        verify(mainCtrlMock).updateLanguage(bundleMock);
    }

    @Test
    public void testShowAdminLogIn() {
        // When
        mainCtrlMock.showAdminLogIn();

        // Then
        verify(mainCtrlMock).showAdminLogIn();
    }

    @Test
    public void testShowAdminCurrentEvents() {
        // When
        mainCtrlMock.showAdminCurrentEvents();

        // Then
        verify(mainCtrlMock).showAdminCurrentEvents();
    }

}
