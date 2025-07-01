package client.scenes;

import client.utils.LanguageManager;
import client.utils.LocalStorage;
import client.utils.ServerUtils;
import client.utils.UserServer;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import commons.User;
import jakarta.ws.rs.core.Response;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.*;

public class StartScreenCtrl implements Initializable {

    private final ServerUtils server;

    private final MainCtrl mainCtrl;
    @FXML
    AnchorPane root;
    @FXML
    TextField createEventField;
    @FXML
    TextField joinEventField;
    @FXML
    ListView<Event> joinedEventsList;
    private User user;
    private String currentServerUrl;
    @FXML
    private Label createLabel;
    @FXML
    private Label joinLabel;
    @FXML
    private Label recentLabel;
    @FXML
    private Button createEventButton;
    @FXML
    private Button joinEventButton;
    @FXML
    private VBox mainContainer;

    @FXML
    private Button settings;

    private Tooltip createEventTooltip;
    private Tooltip joinEventTooltip;
    private Tooltip leaveEventTooltip;
    private Tooltip settingsTooltip;
    private Tooltip eventOverviewTooltip;
    private Tooltip redirectEventTooltip;


    @Inject
    public StartScreenCtrl(ServerUtils server, MainCtrl mainCtrl, Button createEventButton) {
        this.mainCtrl = mainCtrl;
        this.server = server;

        this.createEventButton = createEventButton;

        createEventTooltip = new Tooltip();
        joinEventTooltip = new Tooltip();
        leaveEventTooltip = new Tooltip();
        settingsTooltip = new Tooltip();
        eventOverviewTooltip = new Tooltip();
        redirectEventTooltip = new Tooltip();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupServer();
        configureListView();
        loadUserData();
        initializeShortcuts();
        applyTooltips();

        ImageView imageView = new ImageView(
                new Image(
                        Objects.requireNonNull(
                                getClass()
                                        .getResourceAsStream(
                                                "/client/assets/gear-icon.png")),
                        50, 50, true, true));
        settings.getStyleClass().add("settings-button");
        settings.setGraphic(imageView);
    }

    private void setupServer() {
        ServerUtils.setServer(LanguageManager.getInstance().getServer());
    }

    private void applyTooltips() {
        createEventButton.setTooltip(createEventTooltip);
        joinEventButton.setTooltip(joinEventTooltip);
        settings.setTooltip(settingsTooltip);

    }

    private void initializeShortcuts() {
        //Set up event handlers
        createEventField.setOnKeyPressed(
                event -> {
                    if (event.getCode() ==
                            KeyCode.ENTER) {
                        handleCreateEvent(new ActionEvent());
                    } else if (event.getCode() ==
                            KeyCode.DOWN) {
                        joinEventField.requestFocus();
                    } else if (event.getCode() == KeyCode.UP) {
                        settings.requestFocus();
                    }
                }
        );
        settings.setOnKeyPressed(
                event -> {
                    if (event.getCode() ==
                            KeyCode.DOWN) {
                        createEventField.requestFocus();
                    }
                }
        );
        joinEventField.setOnKeyPressed(
                event -> {
                    if (event.getCode() ==
                            KeyCode.ENTER) {
                        handleJoinEvent(new ActionEvent());
                    } else if (event.getCode() ==
                            KeyCode.UP) {
                        createEventField.requestFocus();
                    } else if (event.getCode() == KeyCode.DOWN) {
                        joinedEventsList.requestFocus();
                    }
                }
        );
        joinedEventsList.setOnKeyPressed(
                event -> {
                    if (event.getCode() ==
                            KeyCode.UP) {
                        joinEventField.requestFocus();
                    }
                }
        );
        root.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() ==
                    KeyCode.ESCAPE) {
                exitApp();
            }
        });

    }

    @FXML
    private void openSettings(ActionEvent event) {
        mainCtrl.showSettings();
    }

    public void updateUIWithBundle(ResourceBundle bundle) {
        createLabel.setText(bundle.getString("createLabel"));
        joinLabel.setText(bundle.getString("joinLabel"));
        recentLabel.setText(bundle.getString("recentLabel"));
        createEventField.setPromptText(bundle.getString("createEventField"));
        createEventButton.setText(bundle.getString("createEventButton"));
        joinEventField.setPromptText(bundle.getString("joinEventField"));
        joinEventButton.setText(bundle.getString("joinEventButton"));
        settingsTooltip.setText(bundle.getString("settingsTooltip"));
        createEventTooltip.setText(bundle.getString("createEventTooltip"));
        joinEventTooltip.setText(bundle.getString("joinEventTooltip"));
        leaveEventTooltip.setText(bundle.getString("leaveEventTooltip"));
        eventOverviewTooltip.setText(bundle.getString("eventOverviewTooltip"));
        redirectEventTooltip.setText(bundle.getString("redirectEventTooltip"));
    }

    private void exitApp() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Application");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit the application?");

        ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == buttonTypeYes) {
                System.exit(0);
            }
        });
    }

    private void configureListView() {
        joinedEventsList.setCellFactory(lv -> new EventCell());
    }

    private void loadUserData() {
        currentServerUrl = LanguageManager.getInstance().getServer(); // This needs to be adjusted based on actual implementation
        Map<String, User> users = LocalStorage.readAllUsers();
        user = users.getOrDefault(currentServerUrl, new User()); // Defaults to an empty user if none found
        if (user == null || user.getId() == null) { // Check if the user needs to be created
            user = server.createUser(new User()); // Adjust this to handle user creation logic
            LocalStorage.storeUser(new UserServer(user, currentServerUrl)); // Store new user
        }
        refreshJoinedEvents();
    }

    private void refreshJoinedEvents() {
        if (user != null) {
            List<Event> events = server.getEventsByUser(this.user);
            joinedEventsList.getItems().setAll(events);
        }
    }

    public void refresh() {
        clearfields();
        refreshJoinedEvents();
    }

    private void clearfields() {
        createEventField.clear();
        joinEventField.clear();
    }

    private Button createLeaveEventButton(Event event) {
        Button removeButton = new Button("X");
        removeButton.getStyleClass().add("remove-button");
        removeButton.setOnAction(e -> leaveEvent(event));
        removeButton.setTooltip(leaveEventTooltip);
        return removeButton;
    }

    private void leaveEvent(Event event) {
        Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        Alert confirmAlert = null;
        Optional<ButtonType> result = null;
        String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
        switch (l) {
            case "en":
                confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to leave this event?", ButtonType.YES, ButtonType.NO);
                confirmAlert.setTitle("Confirmation");
                confirmAlert.setHeaderText("Confirmation");
                confirmAlert.getButtonTypes().setAll(new ButtonType("Yes", ButtonBar.ButtonData.YES), new ButtonType("No", ButtonBar.ButtonData.NO));
                result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    if (attemptToLeaveEvent(event)) {
                        showAlert("You have left the event.", Alert.AlertType.INFORMATION);
                        refreshJoinedEvents();
                    } else {
                        showAlert("Failed to leave event. Please try again.", Alert.AlertType.ERROR);
                    }
                }
                break;
            case "de":
                confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Sind Sie sicher, dass Sie dieses Ereignis verlassen möchten?", ButtonType.YES, ButtonType.NO);
                confirmAlert.setTitle("Bestätigung");
                confirmAlert.setHeaderText("Bestätigung");
                confirmAlert.getButtonTypes().setAll(new ButtonType("Ja", ButtonBar.ButtonData.YES), new ButtonType("Nein", ButtonBar.ButtonData.NO));
                result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    if (attemptToLeaveEvent(event)) {
                        showAlert("Sie haben das Ereignis verlassen.", Alert.AlertType.INFORMATION);
                        refreshJoinedEvents();
                    } else {
                        showAlert("Fehler beim Verlassen des Ereignisses. Bitte versuchen Sie es erneut.", Alert.AlertType.ERROR);
                    }
                }
                break;
            case "fr":
                confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Êtes-vous sûr de vouloir quitter cet événement ?", ButtonType.YES, ButtonType.NO);
                confirmAlert.setTitle("Confirmation");
                confirmAlert.setHeaderText("Confirmation");
                confirmAlert.getButtonTypes().setAll(new ButtonType("Oui", ButtonBar.ButtonData.YES), new ButtonType("Non", ButtonBar.ButtonData.NO));
                result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    if (attemptToLeaveEvent(event)) {
                        showAlert("Vous avez quitté l'événement.", Alert.AlertType.INFORMATION);
                        refreshJoinedEvents();
                    } else {
                        showAlert("Échec de la sortie de l'événement. Veuillez réessayer.", Alert.AlertType.ERROR);
                    }
                }
                break;
            case "nl":
                confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Weet u zeker dat u dit evenement wilt verlaten?", ButtonType.YES, ButtonType.NO);
                confirmAlert.setTitle("Bevestiging");
                confirmAlert.setHeaderText("Bevestiging");
                confirmAlert.getButtonTypes().setAll(new ButtonType("Ja", ButtonBar.ButtonData.YES), new ButtonType("Nee", ButtonBar.ButtonData.NO));
                result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    if (attemptToLeaveEvent(event)) {
                        showAlert("U heeft het evenement verlaten.", Alert.AlertType.INFORMATION);
                        refreshJoinedEvents();
                    } else {
                        showAlert("Kan evenement niet verlaten. Probeer het opnieuw.", Alert.AlertType.ERROR);
                    }
                }
                break;
        }

    }

    private boolean attemptToLeaveEvent(Event event) {
        Response resp = server.leaveEvent(event.getId(), user.getId());
        return resp.getStatus() == 200;
    }

    private void showAlert(String message, Alert.AlertType type) {
        var alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void handleCreateEvent(ActionEvent actionEvent) {
        if (createEventField.getText().isEmpty()) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Event name cannot be empty");
            alert.showAndWait();
            return;
        }

        // Create event logic here
        String eventName = createEventField.getText();
        Event event = new Event(eventName);
        mainCtrl.showContactDetails(user, event, true);
    }

    public void handleJoinEvent(ActionEvent actionEvent) {

        // Join event logic here
        String inviteCode = joinEventField.getText();
        Participant participant;

        try {
            Event event = server.getEventByInviteCode(inviteCode);

            List<Participant> participantsInEvent = server.getParticipantsFromEvent(event.getId());


            // check if this user is already a participant in the event
            for (Participant p : participantsInEvent) {
                if (p.getUser().getId().equals(user.getId())) {
                    // show alert and show event overview
                    var alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("You are already a participant in this event.");
                    alert.showAndWait();
                    mainCtrl.showEventOverview(user, event);
                    return;
                }
            }
            mainCtrl.showContactDetails(user, event, false);
        } catch (Exception e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
            switch (l) {
                case "en":
                    alert.setContentText("The invite code does not exist!");
                    break;
                case "de":
                    alert.setContentText("Der Einladungscode existiert nicht!");
                    break;
                case "fr":
                    alert.setContentText("Le code d'invitation n'existe pas!");
                    break;
                case "nl":
                    alert.setContentText("De invitatiecode bestaat niet!");
                    break;
            }
            alert.showAndWait();
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    private class EventCell extends ListCell<Event> {
        @Override
        protected void updateItem(Event event, boolean empty) {
            super.updateItem(event, empty);
            if (empty || event == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox cellBox = new HBox(10);
                cellBox.setAlignment(Pos.CENTER_LEFT);
                Label eventName = new Label(event.getTitle());
                Button linkButton = new Button("",
                        new ImageView(
                                new Image(
                                        Objects.requireNonNull(
                                                getClass()
                                                        .getResourceAsStream(
                                                                "/client/assets/link-icon.png")),
                                        50, 50, true, true)));
                linkButton.getStyleClass().add("link-button");
                linkButton.setOnAction(e -> mainCtrl.showEventOverview(user, event));
                Button removeButton = createLeaveEventButton(event);
                linkButton.setTooltip(redirectEventTooltip);
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                cellBox.getChildren().addAll(eventName, linkButton, spacer, removeButton);
                setGraphic(cellBox);
            }
        }
    }
}
