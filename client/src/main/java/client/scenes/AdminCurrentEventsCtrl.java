package client.scenes;

import client.utils.JsonWrapperClass;
import client.utils.LocalStorage;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.*;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class AdminCurrentEventsCtrl implements Initializable {

    final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    public Label eventsLabel;
    @FXML
    public TableView<Event> tableEvents;
    @FXML
    public TableColumn <Event, String> tableTitleColumn;
    @FXML
    public TableColumn <Event, Date> tableCreationDateColumn;
    @FXML
    public TableColumn <Event, Date> tableLastActivityColumn;
    @FXML
    public TableColumn <Event, String> tableInviteCodeColumn;
    @FXML
    public Label noEventsLabel;
    @FXML
    public Button importEvent;
    @FXML
    Button leaveAdmin;
    @FXML
    Label eventsLabel1;
    Alert alertConfirmation;
    @FXML
    private AnchorPane root;

    @Inject
    public AdminCurrentEventsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }
    public void refresh(){
        setTable();
    }
    @FXML
    public void leaveAdminIsPressed() {
        alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
        switch (l){
            case "en":
                alertConfirmation.setTitle("Confirmation Dialog");
                alertConfirmation.setHeaderText("Admin Mode");
                alertConfirmation.setContentText("Are you sure you want to leave the Admin Mode?");
                alertConfirmation.getButtonTypes().setAll(new ButtonType("Yes", ButtonBar.ButtonData.YES),
                        new ButtonType("No", ButtonBar.ButtonData.NO));
                break;
            case "de":
                alertConfirmation.setTitle("Bestätigungsdialog");
                alertConfirmation.setHeaderText("Admin-Modus");
                alertConfirmation.setContentText("Sind Sie sicher, dass Sie den Admin-Modus verlassen möchten?");
                alertConfirmation.getButtonTypes().setAll(new ButtonType("Ja", ButtonBar.ButtonData.YES),
                        new ButtonType("Nein", ButtonBar.ButtonData.NO));
                break;
            case "fr":
                alertConfirmation.setTitle("Boîte de dialogue de confirmation");
                alertConfirmation.setHeaderText("Mode administrateur");
                alertConfirmation.setContentText("Êtes-vous sûr de vouloir quitter le mode administrateur ?");
                alertConfirmation.getButtonTypes().setAll(new ButtonType("Oui", ButtonBar.ButtonData.YES),
                        new ButtonType("Non", ButtonBar.ButtonData.NO));
                break;
            case "nl":
                alertConfirmation.setTitle("Bevestigingsvenster");
                alertConfirmation.setHeaderText("Beheerdersmodus");
                alertConfirmation.setContentText("Weet u zeker dat u de beheerdersmodus wilt verlaten?");
                alertConfirmation.getButtonTypes().setAll(new ButtonType("Ja", ButtonBar.ButtonData.YES),
                        new ButtonType("Nee", ButtonBar.ButtonData.NO));
                break;
        }

        alertConfirmation.showAndWait().ifPresent(response -> {
            if (response.getButtonData().equals(ButtonBar.ButtonData.YES)) {
                mainCtrl.showSettings();
            }
        });
    }
    @FXML
    public void deleteButtonPressed(Event eventToDelete){
        alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
        switch (l) {
            case "en":
                alertConfirmation.setTitle("Confirmation Dialog");
                alertConfirmation.setHeaderText("Delete Confirmation");
                alertConfirmation.setContentText("Are you sure you want to delete the Event?");
                alertConfirmation.getButtonTypes().setAll(new ButtonType("Yes", ButtonBar.ButtonData.YES),
                        new ButtonType("No", ButtonBar.ButtonData.NO));
                break;
            case "de":
                alertConfirmation.setTitle("Bestätigungsdialog");
                alertConfirmation.setHeaderText("Löschbestätigung");
                alertConfirmation.setContentText("Möchten Sie das Ereignis wirklich löschen?");
                alertConfirmation.getButtonTypes().setAll(new ButtonType("Ja", ButtonBar.ButtonData.YES),
                        new ButtonType("Nein", ButtonBar.ButtonData.NO));
                break;
            case "fr":
                alertConfirmation.setTitle("Boîte de dialogue de confirmation");
                alertConfirmation.setHeaderText("Confirmation de suppression");
                alertConfirmation.setContentText("Êtes-vous sûr de vouloir supprimer l'événement ?");
                alertConfirmation.getButtonTypes().setAll(new ButtonType("Oui", ButtonBar.ButtonData.YES),
                        new ButtonType("Non", ButtonBar.ButtonData.NO));
                break;
            case "nl":
                alertConfirmation.setTitle("Bevestigingsvenster");
                alertConfirmation.setHeaderText("Verwijderingsbevestiging");
                alertConfirmation.setContentText("Weet u zeker dat u het evenement wilt verwijderen?");
                alertConfirmation.getButtonTypes().setAll(new ButtonType("Ja", ButtonBar.ButtonData.YES),
                        new ButtonType("Nee", ButtonBar.ButtonData.NO));
                break;
        }

        alertConfirmation.showAndWait().ifPresent(response2 -> {
            if (response2.getButtonData().equals(ButtonBar.ButtonData.YES)) {
                try {
                    Response response = server.deleteEvent(eventToDelete);
                } catch (WebApplicationException e) {
                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                    return;
                }
                refresh();
            }
        });
    }
    @FXML
    public void downloadButtonIsPressed(Event eventToDownload){
        //need to save Event JSON file
        Long eventId = eventToDownload.getId();
        JsonWrapperClass wrapClass = new JsonWrapperClass();
        // 6, now 4
        wrapClass.setTransfers(server.getAllTransfersFromEvent(eventId));
        wrapClass.setDebts(server.getAllDebtsFromEvent(eventId));
        List<Tag> tags = server.getAllTagsFromEvent(eventId);
        wrapClass.setTags(tags);
        wrapClass.setExpenses(server.getAllExpensesFromEvent(eventId));
        wrapClass.setParticipants(server.getParticipantsFromEvent(eventId));
        wrapClass.setEvent(eventToDownload);

        String jsonString = LocalStorage.toJson(wrapClass);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("eventData.json");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = (Stage) tableEvents.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                // Write JSON string to the selected file
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(jsonString);
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    @FXML
    public void importEventIsPressed() {
        String jsonString = getJsonString();
        if(jsonString == null)
            // need to work on it
            return;
        JsonWrapperClass jsonWrapperClass = LocalStorage.fromJson(jsonString);
        restoreEvent(jsonWrapperClass);
    }

    public String getJsonString(){
        FileChooser fileChooser = new FileChooser();
        String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
        switch (l) {
            case "en":
                fileChooser.setTitle("Select JSON file");
                break;
            case "de":
                fileChooser.setTitle("Wähle JSON-Datei aus");
                break;
            case "fr":
                fileChooser.setTitle("Sélectionner le fichier JSON");
                break;
            case "nl":
                fileChooser.setTitle("Selecteer JSON-bestand");
                break;
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
        Stage stage = (Stage) tableEvents.getScene().getWindow();

        // Show the file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                // Read the JSON string from the selected file
                StringBuilder jsonStringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonStringBuilder.append(line);
                }
                String jsonString = jsonStringBuilder.toString();
                return jsonString;
                // Process the imported JSON data as needed
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public void restoreEvent(JsonWrapperClass wrapperClass){
        Event event = wrapperClass.getEvent();
        Participant owner = null;
        List<Participant> participants = wrapperClass.getParticipants();
        List<Expense> expenses = wrapperClass.getExpenses();
        List<Tag> tags = wrapperClass.getTags();
        List<Debt> debts = wrapperClass.getDebts();
        List<Transfer> transfers = wrapperClass.getTransfers();

        //search for owner to find the user
        for(Participant p: participants){
            if(p.isOwner()){
                owner = p;
                break;
            }
        }

        HashMap<Participant, Participant> oldToNewParticipant = new HashMap<>();
        HashMap<Expense, Expense> oldToNewExpense = new HashMap<>();
        HashMap<Debt, Debt> oldToNewDebt = new HashMap<>();
        HashMap<Tag, Tag> oldToNewTag = new HashMap<>();

        //add event
        event = server.addEvent(event, Objects.requireNonNull(owner).getUser());

        //add participants
        for(int i = 0; i < participants.size(); ++i){
            Participant p2 = new Participant(participants.get(i));
            participants.get(i).setEvent(event);
            participants.set(i, server.addParticipant(participants.get(i)));
            oldToNewParticipant.put(p2, participants.get(i));

        }
        //add tags
        for(Tag t: tags){
            //old copy
            Tag t2 = new Tag(t);
            t.setEvent(event);
            t.setExpenses(new HashSet<Expense>());
            t = server.addTag(event.getId(), t);
            oldToNewTag.put(t2, t);
        }
        //add expenses
        for(Expense e: expenses) {
            Expense old = new Expense(e);
            List<Tag> tagsInExpense = e.getTags();

            e.setEvent(event);
            e.setPayer(oldToNewParticipant.get(e.getPayer()));
            e.getParticipants().replaceAll(oldToNewParticipant::get);
            e.getTags().replaceAll(oldToNewTag::get);
            e = server.addExpenseWithoutDebts(e, event);
            for(Tag t: e.getTags()){
                server.addTag(event.getId(), t);
            }
            oldToNewExpense.put(old, e);
        }
        //add debts
        for(Debt d: debts) {
            Debt old = new Debt(d);
            d.setEvent(event);
            d.setExpense(oldToNewExpense.get(d.getExpense()));
            d.setParticipant(oldToNewParticipant.get(d.getParticipant()));
            d.setReceiver(oldToNewParticipant.get(d.getReceiver()));
            d = server.addDebt(event.getId(), d);
            oldToNewDebt.put(old, d);
        }
        //add transfers
        for(Transfer t: transfers){
            t.setEvent(event);
            t.setDebt(oldToNewDebt.get(t.getDebt()));
            t = server.addTransfer(event.getId(), t.getDebt().getId(), t);
        }
        refresh();
    }
    public void setTable(){
        List<Event> eventList;
        try {
            eventList = server.getAllEvents();
        } catch(Exception e){
            eventList = new ArrayList<>();
        }
        if(eventList.isEmpty()) {
            tableEvents.setVisible(false); // Hide the TableView
            noEventsLabel.setVisible(true); // Show the message
        } else {
            // there are events
            tableEvents.setVisible(true); // Show the TableView
            tableEvents.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
            noEventsLabel.setVisible(false);
            ObservableList<Event> data = FXCollections.observableList(eventList);
            tableEvents.setItems(data);
            setButtons();
        }
    }

    TableColumn<Event, Void> deleteButtonColumn;
    TableColumn<Event, Void> downloadButtonColumn;
    public void setButtons() {
        if (downloadButtonColumn != null) {
            tableEvents.getColumns().remove(downloadButtonColumn);
        }
        if (deleteButtonColumn != null) {
            tableEvents.getColumns().remove(deleteButtonColumn);
        }

        deleteButtonColumn = new TableColumn<>(getLocalizedText("Delete"));
        downloadButtonColumn = new TableColumn<>(getLocalizedText("Download JSON"));

        deleteButtonColumn.setCellFactory(param -> new TableCellWithDeleteButton());
        downloadButtonColumn.setCellFactory(param -> new TableCellwithDownloadButton());

        tableEvents.getColumns().addAll(deleteButtonColumn, downloadButtonColumn);
    }

    private String getLocalizedText(String key) {
        String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
        switch (l) {
            case "en":
                switch (key) {
                    case "Download JSON":
                        return "Download JSON";
                    case "Delete":
                        return "Delete";
                }
                break;
            case "de":
                switch (key) {
                    case "Download JSON":
                        return "JSON herunterladen";
                    case "Delete":
                        return "Löschen";
                }
                break;
            case "fr":
                switch (key) {
                    case "Download JSON":
                        return "Télécharger JSON";
                    case "Delete":
                        return "Supprimer";
                }
                break;
            case "nl":
                switch (key) {
                    case "Download JSON":
                        return "JSON downloaden";
                    case "Delete":
                        return "Verwijderen";
                }
                break;
        }

        return "";

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeShortcuts();
    }

    private void initializeShortcuts() {
        root.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                importEventIsPressed();
            }
            if (event.getCode() ==
                    KeyCode.BACK_SPACE ||
                    event.getCode() == KeyCode.ESCAPE) {
                leaveAdminIsPressed();
            }
            if (event.getCode() == KeyCode.DELETE) {
                Event eventToDelete = tableEvents.getSelectionModel().getSelectedItem();
                deleteButtonPressed(eventToDelete);
            }
        });
        leaveAdmin.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                leaveAdminIsPressed();
            }
        });
    }


    private class TableCellWithDeleteButton extends TableCell<Event, Void> {
        private final Button button;

        public TableCellWithDeleteButton() {
            String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
            String text = "";
            switch (l) {
                case "en":
                    text = "Delete";
                    break;
                case "de":
                    text = "Löschen";
                    break;
                case "fr":
                    text = "Supprimer";
                    break;
                case "nl":
                    text = "Verwijderen";
                    break;
            }
            this.button = new Button(text);
            this.button.setOnAction(event -> {
                Event eventToDelete = getTableView().getItems().get(getIndex());
                deleteButtonPressed(eventToDelete);
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(button);
            }
        }
    }

    private class TableCellwithDownloadButton extends TableCell<Event, Void> {
        private final Button button;

        public TableCellwithDownloadButton() {
            String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
            String text = "";
            switch (l) {
                case "en":
                    text = "Download";
                    break;
                case "de":
                    text = "Herunterladen";
                    break;
                case "fr":
                    text = "Télécharger";
                    break;
                case "nl":
                    text = "Downloaden";
                    break;
            }
            this.button = new Button(text);
            this.button.setOnAction(event -> {
                Event eventToDownload = getTableView().getItems().get(getIndex());
                downloadButtonIsPressed(eventToDownload);
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(button);
            }
        }
    }

    public void updateUIWithBundle(ResourceBundle bundle) {
        eventsLabel.setText(bundle.getString("eventsLabel"));
        noEventsLabel.setText(bundle.getString("noEventsLabel"));
        tableTitleColumn.setText(bundle.getString("tableTitleColumn"));
        tableCreationDateColumn.setText(bundle.getString("tableCreationDateColumn"));
        tableLastActivityColumn.setText(bundle.getString("tableLastActivityColumn"));
        tableInviteCodeColumn.setText(bundle.getString("tableInviteCodeColumn"));
        leaveAdmin.setText(bundle.getString("leaveAdmin"));
        eventsLabel1.setText(bundle.getString("eventsLabel1"));
        importEvent.setText(bundle.getString("importEvent"));
    }
}
