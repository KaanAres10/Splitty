package client.scenes;


import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.User;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;






import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class CurrentParticipantsCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Event event;
    private User user;
    @FXML
    Button backButton;
    @FXML
    Button addButton;
    @FXML
    public Label eventNameLabel;
    @FXML
    public Label noLabel;
    @FXML
    public TableView<Participant> tableParticipants;
    @FXML
    public TableColumn <Participant, String> tableNameColumn;
    @FXML
    public TableColumn <Participant, String> tableEmailColumn;
    @FXML
    public TableColumn <Participant, String> tableIbanColumn;
    @FXML
    public TableColumn <Participant, String> tableBicColumn;

    @FXML
    private Pane root;

    Alert alertConfirmation;

    @Inject
    public CurrentParticipantsCtrl(ServerUtils server, MainCtrl mainCtrl, Event event, User user) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.event = event;
        this.user = user;
    }

    public void refresh(User user, Event event){
        this.user = user;
        this.event = event;
        eventNameLabel.setText(event.getTitle());
        setTable();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeShortcuts();
    }

    private void initializeShortcuts() {
        root.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() ==
                    KeyCode.ESCAPE ||
                    event.getCode() ==
                            KeyCode.BACK_SPACE){
                backButtonPressed();
            }
        });
        addButton.addEventFilter(KeyEvent.KEY_PRESSED, event-> {
            if (event.getCode() ==
                    KeyCode.ENTER) {
                addButtonPressed();
            }
        });
        root.addEventFilter(KeyEvent.KEY_PRESSED, event-> {
            if (event.getCode() ==
                    KeyCode.E) {
                editButtonPressed(tableParticipants.getSelectionModel().getSelectedItem());
            }
        });
        tableParticipants.addEventFilter(KeyEvent.KEY_PRESSED, event-> {
            if (event.getCode() ==
                    KeyCode.DOWN) {
                addButton.requestFocus();
            } else if (event.getCode() ==
                    KeyCode.UP) {
                backButton.requestFocus();
            }
        });
    }
    @FXML
    public void backButtonPressed(){
        mainCtrl.showEventOverview(user, event);
    }
    @FXML
    public void addButtonPressed(){
        mainCtrl.showContactDetails(user, event);
    }
    public void editButtonPressed(Participant participant){
        mainCtrl.showContactDetails(participant);
    }
    public void deleteButtonPressed(Participant participant){
            alertConfirmation = new Alert(AlertType.CONFIRMATION);
            String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
            switch (l) {
                case "en":
                    alertConfirmation.setTitle("Confirmation Dialog");
                    alertConfirmation.setHeaderText("Delete Confirmation");
                    alertConfirmation.setContentText("Are you sure you want to delete the Participant?");
                    alertConfirmation.getButtonTypes().setAll(new ButtonType("Yes", ButtonBar.ButtonData.YES),
                            new ButtonType("No", ButtonBar.ButtonData.NO));
                    break;
                case "de":
                    alertConfirmation.setTitle("Bestätigung");
                    alertConfirmation.setHeaderText("Löschen Bestätigung");
                    alertConfirmation.setContentText("Sind Sie sicher, dass Sie den Teilnehmer löschen möchten?");
                    alertConfirmation.getButtonTypes().setAll(new ButtonType("Ja", ButtonBar.ButtonData.YES),
                            new ButtonType("Nein", ButtonBar.ButtonData.NO));
                    break;
                case "fr":
                    alertConfirmation.setTitle("Dialogue de Confirmation");
                    alertConfirmation.setHeaderText("Confirmation de Suppression");
                    alertConfirmation.setContentText("Êtes-vous sûr de vouloir supprimer le participant ?");
                    alertConfirmation.getButtonTypes().setAll(new ButtonType("Oui", ButtonBar.ButtonData.YES),
                            new ButtonType("Non", ButtonBar.ButtonData.NO));
                    break;
                case "nl":
                    alertConfirmation.setTitle("Bevestigingsvenster");
                    alertConfirmation.setHeaderText("Bevestiging verwijderen");
                    alertConfirmation.setContentText("Weet u zeker dat u de deelnemer wilt verwijderen?");
                    alertConfirmation.getButtonTypes().setAll(new ButtonType("Ja", ButtonBar.ButtonData.YES),
                            new ButtonType("Nee", ButtonBar.ButtonData.NO));
                    break;
            }
            alertConfirmation.showAndWait().ifPresent(response2 -> {
                if (response2.getButtonData().equals(ButtonBar.ButtonData.YES)) {
                    try {
                        if(canBeDeleted(participant)) {
                            Response response = server.deleteParticipant(participant);
                        } else {
                            Alert alert = new Alert(AlertType.ERROR);
                            switch (l) {
                                case "en":
                                    alert.setContentText("There is an expense including this participant. " +
                                            "The participant can not be deleted!");
                                    break;
                                case "de":
                                    alert.setContentText("Es gibt eine Ausgabe, die diesen Teilnehmer einschließt. " +
                                            "Der Teilnehmer kann nicht gelöscht werden!");
                                    break;
                                case "fr":
                                    alert.setContentText("Il y a une dépense incluant ce participant. " +
                                            "Le participant ne peut pas être supprimé!");
                                    break;
                                case "nl":
                                    alert.setContentText("Er zijn kosten inclusief deze deelnemer. " +
                                            "De deelnemer kan niet worden verwijderd!");
                                    break;
                            }
                            alert.showAndWait();
                        }
                    } catch (WebApplicationException e) {
                        var alert = new Alert(Alert.AlertType.ERROR);
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                        return;
                    }
                    refresh(user, event);
                }
            });

    }
    public boolean canBeDeleted(Participant participant){
        List<Expense> expenses = server.getAllExpensesFromEvent(event.getId());
        //check if the participant is a payer in current expense
        for(Expense e: expenses){
            if(Objects.equals(e.getPayer(), participant))
                return false;
        }
        //check if the participant is just in the current expense
        for(Expense e: expenses){
            if(e.getParticipants() != null) {
                for (Participant p : e.getParticipants()) {
                    if (Objects.equals(participant, p))
                        return false;
                }
            }
        }
        return true;
    }
    public void setTable(){
        List<Participant> participantsInEvent;
        try{
            participantsInEvent = server.getParticipantsFromEvent(event.getId());
        }catch(Exception e){
            participantsInEvent = new ArrayList<>();
        }
        if(participantsInEvent.isEmpty()){
            tableParticipants.setVisible(false); // Hide the TableView
            noLabel.setVisible(true); // Show the message
        } else {
            tableParticipants.setVisible(true); // Show the TableView
            tableParticipants.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
            noLabel.setVisible(false);
            ObservableList<Participant> data = FXCollections.observableList(participantsInEvent);
            tableParticipants.setItems(data);
            setButtons();
        }

    }

    private TableColumn<Participant, Void> editButtonColumn;
    private TableColumn<Participant, Void> deleteButtonColumn;
    public void setButtons() {
        if (editButtonColumn != null) {
            tableParticipants.getColumns().remove(editButtonColumn);
        }
        if (deleteButtonColumn != null) {
            tableParticipants.getColumns().remove(deleteButtonColumn);
        }

        editButtonColumn = new TableColumn<>(getLocalizedText("Edit"));
        deleteButtonColumn = new TableColumn<>(getLocalizedText("Delete"));

        editButtonColumn.setCellFactory(param -> new TableCellWithEditButton());
        deleteButtonColumn.setCellFactory(param -> new TableCellWithDeleteButton());

        tableParticipants.getColumns().addAll(editButtonColumn, deleteButtonColumn);
    }

    String getLocalizedText(String key) {
        String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
        switch (l) {
            case "en":
                switch (key) {
                    case "Edit":
                        return "Edit";
                    case "Delete":
                        return "Delete";
                }
                break;
            case "de":
                switch (key) {
                    case "Edit":
                        return "Bearbeiten";
                    case "Delete":
                        return "Löschen";
                }
                break;
            case "fr":
                switch (key) {
                    case "Edit":
                        return "Éditer";
                    case "Delete":
                        return "Supprimer";
                }
                break;
            case "nl":
                switch (key) {
                    case "Edit":
                        return "Bewerken";
                    case "Delete":
                        return "Verwijderen";
                }
                break;
        }
        return "";
    }


    public void updateUIWithBundle(ResourceBundle bundle) {
        eventNameLabel.setText(bundle.getString("eventNameLabel"));
        backButton.setText(bundle.getString("backButton"));
        addButton.setText(bundle.getString("addButton"));
        tableNameColumn.setText(bundle.getString("tableNameColumn"));
        noLabel.setText(bundle.getString("noLabel"));
    }

    private class TableCellWithEditButton extends TableCell<Participant, Void> {
        private final Button editButton;

        public TableCellWithEditButton() {
            editButton = new Button("");
            editButton.getStyleClass().add("small-button");// Apply a CSS class for styling
            ImageView imageView1 = new ImageView(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/client/assets/edit.png"))));
            imageView1.setFitWidth(14); // Set the width you desire
            imageView1.setFitHeight(16); // S
            editButton.setGraphic(imageView1);
            editButton.setOnAction(event -> {
                Participant participant = getTableView().getItems().get(getIndex());
                editButtonPressed(participant);
            });
            setGraphic(editButton);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(editButton);
            }
        }
    }

    private class TableCellWithDeleteButton extends TableCell<Participant, Void> {
        private final Button deleteButton;

        public TableCellWithDeleteButton() {
            deleteButton = new Button("");
            deleteButton.getStyleClass()
                    .add("small-button"); // Apply a CSS class for styling
            ImageView imageView2 = new ImageView(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/client/assets/bin.png"))));
            imageView2.setFitWidth(14); // Set the width you desire
            imageView2.setFitHeight(16); // S
            deleteButton.setGraphic(imageView2);
            deleteButton.setOnAction(event -> {
                Participant participant = getTableView().getItems().get(getIndex());
                deleteButtonPressed(participant);
            });
            setGraphic(deleteButton);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }


        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(deleteButton);
            }
        }
    }
}