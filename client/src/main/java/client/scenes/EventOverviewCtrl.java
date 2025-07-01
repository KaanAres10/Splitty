package client.scenes;

import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

import static client.scenes.ExpenseCtrl.toCssColor;

public class EventOverviewCtrl implements Initializable {

    ServerUtils server;

    MainCtrl mainCtrl;
    private User user;
    Event event;
    private List<Expense> expenses; // store expenses locally
    private Method lastUsedFilter;


    @FXML
    private Button showStatisticsBtn;
    @FXML
    private Label eventTitle;
    @FXML
    private Label participants;
    @FXML
    ComboBox<String> participantsList;
    @FXML
    private ListView<HBox> expenseList;
    @FXML
    private TableView<TableRow> expenseTable;
    @FXML
    private TableColumn<TableRow, String> dateColumn;
    @FXML
    private TableColumn<TableRow, String> descriptionColumn;
    @FXML
    private TableColumn<TableRow, String> participantsColumn;
    @FXML
    private TableColumn<TableRow, TextFlow> tagsColumn;
    @FXML
    private TableColumn<TableRow, Button> editColumn;
    @FXML
    public TableColumn<TableRow, Button> deleteColumn;

    @FXML
    private Button fromPersonBtn;
    @FXML
    private Button includingPersonBtn;
    @FXML
    private Label participantLabel;
    @FXML
    private Button participantAdd;
    @FXML
    private Label expenseLabel;
    @FXML
    private Button sendInvite;
    @FXML
    private Button addExpense;
    @FXML
    private Button allTable;
    @FXML
    private Button settleDebts;
    @FXML
    private Button backBtn;
    @FXML
    private Pane root;
    @FXML
    private TextField changeTitleField;
    @FXML
    private Button changeTitleBtn;
    ObservableList<Participant> participantsInEvent;


    @Inject
    public EventOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeShortcuts();
    }

    public void stop() {
        server.stop();
    }

    public void refresh(User user, Event event) {
        this.event = event;
        this.user = user;
        List<Participant> data;
        try {
            data = server.getParticipantsFromEvent(event.getId());
        } catch (Exception e) {
            data = new ArrayList<>();
        }
        participantsInEvent = FXCollections.observableArrayList(data);
        server.getParticipantsUpdate(event.getId(), p -> {
            participantsInEvent.add(p);
            try {
                Platform.runLater(() -> initPage(user, event));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        changeTitleBtn.setText("");
        changeTitleBtn.getStyleClass().add("small-button");// Apply a CSS class for styling
        ImageView imageView1 = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/client/assets/edit.png"))));
        imageView1.setFitWidth(14); // Set the width you desire
        imageView1.setFitHeight(16); // S
        changeTitleBtn.setGraphic(imageView1);


        // register for updates from websocket
        registerForWebSocketExpenseUpdates(event);

        // register for deletions from websocket
        registerForWebSocketExpenseDeletions(event);

        initPage(user, event);
    }

    private void registerForWebSocketExpenseUpdates(Event event) {
        server.registerForDataWebSocket(
                "/topic/" + event.getId() + "/expenses",
                Expense.class, expense -> {
                    // if expense already in this.expenses update it
                    if (expenses.contains(expense)) {
                        expenses = expenses.stream().map(obj -> {
                            if (Objects.equals(obj.getId(), expense.getId())) return expense;
                            return obj;
                        }).toList();
                    }
                    // else add it
                    else {
                        expenses.add(expense);
                    }

                    // refresh the table view by invoking lastUsedFilter
                    Platform.runLater(() -> {
                        try {
                            lastUsedFilter.invoke(this);
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });
    }

    private void registerForWebSocketExpenseDeletions(Event event) {
        server.registerForDataWebSocket("/topic/" + event.getId() + "/expenses/delete", Expense.class, expense -> {
            // if expense not in this.expenses do nothing
            if (!expenses.contains(expense)) return;
            // else remove from expenses
            expenses = expenses.stream().filter(obj -> {
                return !Objects.equals(obj.getId(), expense.getId());
            }).toList();

            // refresh the table view by invoking lastUsedFilter
            Platform.runLater(() -> {
                try {
                    lastUsedFilter.invoke(this);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }


    /**
     * This method basically puts the passed data into the scene
     *
     * @param event is the data that is passed to the control
     */
    public void initPage(User user, Event event) {
        eventTitle.setText(event.getTitle());
        this.participantsList.getItems().clear();
        String participantsString = "";
        int counter = 0;
        for (Participant p : participantsInEvent) {
            this.participantsList.getItems().add(p.getName());
            participantsString += p.getName();
            if (counter++ < participantsInEvent.size() - 1) participantsString += ", ";
        }
        this.participants.setText(participantsString);
        if (this.participantsList.getItems().size() > 0) {
            this.participantsList.setValue(this.participantsList.getItems().get(0));
        }
        this.participantsList.setOnAction(e -> getChoice(this.participantsList));
        showAllExpensesClicked();
        getChoice(this.participantsList);
    }

    @FXML
    void showStatistics() {
        mainCtrl.showStatistics(event);
    }

    /**
     * Changes the text of the buttons for filtering the expenses
     *
     * @param comboBox contains the chosen participant by the user
     */
    public void getChoice(ComboBox<String> comboBox) {
        String name = comboBox.getValue();
        String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
        switch (l) {
            case "en":
                fromPersonBtn.setText("From " + name);
                includingPersonBtn.setText("Including " + name);
                break;
            case "de":
                fromPersonBtn.setText("Von " + name);
                includingPersonBtn.setText("Einschließlich " + name);
                break;
            case "fr":
                fromPersonBtn.setText("De " + name);
                includingPersonBtn.setText("Incluant " + name);
                break;
            case "nl":
                fromPersonBtn.setText("Van " + name);
                includingPersonBtn.setText("Inclusief " + name);
                break;
        }
    }

    public void showAllExpensesClicked() {
        expenseTable.getItems().clear();
        expenses = server.getExpensesFromEvent(event.getId());
        expenseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        visualizeExpenses(expenses, null);

        try {
            lastUsedFilter = this.getClass().getDeclaredMethod("showAllExpensesClicked");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void showExpensesFromParticipant() {
        expenseTable.getItems().clear();
        expenses = server.getExpensesFromEvent(event.getId());
        String name = this.participantsList.getValue();
        List<Expense> list = expenses.stream().filter(x -> x.getPayer().getName().equals(name)).toList();
        visualizeExpenses(list, null);

        try {
            lastUsedFilter = this.getClass().getDeclaredMethod("showExpensesFromParticipant");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void showExpensesIncludingParticipant() {
        expenseTable.getItems().clear();
        String name = this.participantsList.getValue();
        Participant participant = server.getParticipantByName(this.event.getId(), name);
        expenses = server.getExpensesFromEvent(event.getId());
        visualizeExpenses(expenses, participant);

        try {
            lastUsedFilter = this.getClass().getDeclaredMethod("showExpensesIncludingParticipant");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void visualizeExpenses(List<Expense> list, Participant includedParticipant) {
        List<TableRow> tableRows = new ArrayList<>();
        dateColumn.setCellValueFactory(new PropertyValueFactory<TableRow, String>("date"));
        descriptionColumn.setCellValueFactory(
            new PropertyValueFactory<TableRow, String>("description"));
        participantsColumn.setCellValueFactory(
            new PropertyValueFactory<TableRow, String>("participants"));
        tagsColumn.setCellValueFactory(
                new PropertyValueFactory<TableRow, TextFlow>("tags"));
        editColumn.setCellValueFactory(new PropertyValueFactory<TableRow, Button>("editButton"));
        deleteColumn.setCellValueFactory(new PropertyValueFactory<TableRow, Button>("deleteButton"));

        for (Expense expense : list) {
            List<Debt> debts = server.getDebtsByExpense(event.getId(), expense.getId());
            Set<Participant> set = new HashSet<>();
            set.add(expense.getPayer());
            for (Debt d : debts) {
                set.add(d.getParticipant());
                set.add(d.getReceiver());
            }
            if (includedParticipant != null && !set.contains(includedParticipant)) {
                continue;
            }
            String expenseSummary = expense.getPayer().getName() + " "
                + expense.getAmount() + expense.getCurrency() + " " + expense.getTitle();
            Button editButton = new Button("");
            editButton.getStyleClass().add("small-button");// Apply a CSS class for styling
            ImageView imageView1 = new ImageView(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/client/assets/edit.png"))));
            imageView1.setFitWidth(14); // Set the width you desire
            imageView1.setFitHeight(16); // S
            editButton.setGraphic(imageView1);

            editButton.setOnAction(event -> {
                mainCtrl.showAddEdit(this.user, this.event, expense);
            });

            Button deleteButton = new Button("");
            ImageView imageView2 = new ImageView(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/client/assets/bin.png"))));
            imageView2.setFitWidth(14); // Set the width you desire
            imageView2.setFitHeight(16); // S
            deleteButton.setGraphic(imageView2);
            deleteButton.setOnAction(event -> {
                Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());

                switch (l) {
                    case "en":
                        alertConfirmation.setTitle("Confirmation Dialog");
                        alertConfirmation.setHeaderText("Delete Confirmation");
                        alertConfirmation.setContentText(
                                "Are you sure you want to delete the Expense?");
                        alertConfirmation.getButtonTypes()
                                .setAll(new ButtonType("Yes", ButtonBar.ButtonData.YES),
                                        new ButtonType("No", ButtonBar.ButtonData.NO));
                        break;
                    case "de":
                        alertConfirmation.setTitle("Bestätigung");
                        alertConfirmation.setHeaderText("Löschen Bestätigung");
                        alertConfirmation.setContentText(
                                "Sind Sie sicher, dass Sie die Ausgabe löschen möchten?");
                        alertConfirmation.getButtonTypes()
                                .setAll(new ButtonType("Ja", ButtonBar.ButtonData.YES),
                                        new ButtonType("Nein", ButtonBar.ButtonData.NO));
                        break;
                    case "fr":
                        alertConfirmation.setTitle("Dialogue de Confirmation");
                        alertConfirmation.setHeaderText("Confirmation de Suppression");
                        alertConfirmation.setContentText(
                                "Êtes-vous sûr de vouloir supprimer la dépense?");
                        alertConfirmation.getButtonTypes()
                                .setAll(new ButtonType("Oui", ButtonBar.ButtonData.YES),
                                        new ButtonType("Non", ButtonBar.ButtonData.NO));
                        break;
                    case "nl":
                        alertConfirmation.setTitle("Bevestigingsvenster");
                        alertConfirmation.setHeaderText("Bevestiging verwijderen");
                        alertConfirmation.setContentText(
                                "Weet u zeker dat u de kosten wilt verwijderen?");
                        alertConfirmation.getButtonTypes()
                                .setAll(new ButtonType("Ja", ButtonBar.ButtonData.YES),
                                        new ButtonType("Nee", ButtonBar.ButtonData.NO));
                        break;
                }
                alertConfirmation.showAndWait().ifPresent(response2 -> {
                    if (response2.getButtonData().equals(ButtonBar.ButtonData.YES)) {
                        deleteButtonIsPressed(expense);
                    }
                });
            });
            String dateString = expense.getDate().toString().substring(4, 10);
            StringBuilder sb =new StringBuilder(expense.getDate().toString());
            sb.reverse();
            String reversed = sb.substring(0,4);
            sb = new StringBuilder(reversed).reverse();
            dateString+=" " + sb;
            String participantsTableString = expense.getPayer().getName() + ", ";
            for (int i = 0; i < expense.getParticipants().size(); i++) {
                participantsTableString += expense.getParticipants().get(i).getName();
                if (i < expense.getParticipants().size() - 1) participantsTableString += ", ";
            }
            List<Tag> tags = expense.getTags();
            TextFlow tagsFlow = new TextFlow();
            for(Tag t: tags){
                Text text = new Text(t.getName());
                StackPane stackPane = new StackPane(text);
                stackPane.setPrefWidth(80);
                stackPane.setPrefHeight(28);
                stackPane.setStyle("-fx-background-color: " + toCssColor(t.getColor()) + ";");
                tagsFlow.getChildren().addAll(stackPane);
            }
            TableRow tr = new TableRow(dateString, expenseSummary, participantsTableString, tagsFlow, editButton, deleteButton);
            tableRows.add(tr);
        }
        ObservableList<TableRow> data = FXCollections.observableArrayList(tableRows);
        expenseTable.setItems(data);
    }

    private void deleteButtonIsPressed(Expense expense){

        Response response = server.deleteExpense(event.getId(), expense.getId());

        if(response.getStatus() != 200){
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
            switch (l) {
                case "en":
                    alert.setContentText("It seems the expense has already been deleted!");
                    break;
                case "de":
                    alert.setContentText("Es scheint, dass die Ausgabe bereits gelöscht wurde!");
                    break;
                case "fr":
                    alert.setContentText("Il semble que la dépense ait déjà été supprimée!");
                    break;
                case "nl":
                    alert.setContentText("Het lijkt erop dat de kosten al zijn verwijderd!");
                    break;
            }
            alert.showAndWait();
        }
        refresh(user, event);
    }

    public void sendInvite() {
        mainCtrl.showInvite(event);
    }

    public void addExpense() {
        if (participantsInEvent.size() > 1) {
            mainCtrl.showAddEdit(this.user, this.event, null);
        } else {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Should have at least 2 participant to add expense!");
            alert.showAndWait();
        }
    }

    public void settleDebts() {
        mainCtrl.showDebts(user, event);
    }

    public void editParticipants() {
        mainCtrl.showCurrentParticipants(user, event);
    }

    public void updateUIWithBundle(ResourceBundle bundle) {
        eventTitle.setText(bundle.getString("eventTitle"));
        participantLabel.setText(bundle.getString("ParticipantLabel"));
        participantAdd.setText(bundle.getString("participantAdd"));
        participants.setText(bundle.getString("participants"));
        expenseLabel.setText(bundle.getString("expenseLabel"));
        sendInvite.setText(bundle.getString("sendInvite"));
        addExpense.setText(bundle.getString("addExpense"));
        allTable.setText(bundle.getString("allTable"));
        settleDebts.setText(bundle.getString("settleDebts"));
        backBtn.setText(bundle.getString("backBtn"));
        dateColumn.setText(bundle.getString("dateColumn"));
        participantsColumn.setText(bundle.getString("participantsColumn"));
        tagsColumn.setText(bundle.getString("tagsColumn"));
        editColumn.setText(bundle.getString("editColumn"));
        deleteColumn.setText(bundle.getString("deleteColumn"));
        changeTitleBtn.setText(bundle.getString("changeTitleBtn"));
        showStatisticsBtn.setText(bundle.getString("showStatisticsBtn"));
        tagsColumn.setText(bundle.getString("tagsColumnEvent"));
        participantsColumn.setText(bundle.getString("participantColumnEvent"));
    }

    public void backBtn() {
        mainCtrl.showStartScreen();
    }

    public void initializeShortcuts() {
        root.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.BACK_SPACE ||
                e.getCode() == KeyCode.ESCAPE) {
                backBtn();
            }
        });
        sendInvite.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.DOWN) {
                getFromPersonBtn().requestFocus();
            }
        });
        expenseTable.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.DOWN) {
                settleDebts.requestFocus();
            } else if (e.getCode() ==
                KeyCode.UP) {
                includingPersonBtn.requestFocus();
            }
        });
        fromPersonBtn.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.DOWN) {
                expenseTable.requestFocus();
            }
        });
        includingPersonBtn.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.DOWN) {
                settleDebts.requestFocus();
            }
        });
        settleDebts.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.UP) {
                expenseTable.requestFocus();
            }
        });
        settleDebts.setOnKeyPressed(e -> {
            if (e.getCode() ==
                    KeyCode.ENTER) {
                settleDebts();
            }
        });
        backBtn.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.UP) {
                settleDebts.requestFocus();
            }
        });
        backBtn.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.ENTER) {
                backBtn();
            }
        });
        backBtn.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.DOWN) {
                changeTitleBtn.requestFocus();
            }
        });
        changeTitleBtn.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.ENTER) {
                changeTitle();
            }
        });
        changeTitleBtn.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.DOWN) {
                participantAdd.requestFocus();
            }
        });
        participantAdd.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.UP) {
                changeTitleBtn.requestFocus();
            }
        });
        participantAdd.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.ENTER) {
                editParticipants();
            }
        });
        participantAdd.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.DOWN) {
                allTable.requestFocus();
            }
        });
        changeTitleField.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.DOWN) {
                changeTitleBtn.requestFocus();
            }
        });
        changeTitleField.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.ENTER) {
                changeTitle();
            }
        });
        participantsList.setOnKeyPressed(e -> {
            if (e.getCode() ==
                KeyCode.ENTER) {
                getChoice(participantsList);
            }
        });

        participantsList.setOnKeyPressed(e -> {
            if (e.getCode() ==
                    KeyCode.DOWN) {
                includingPersonBtn.requestFocus();
            }
        });

    }

    public void changeTitle(){
        if(changeTitleField.getText().isEmpty()){
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
            switch (l) {
                case "en":
                    alert.setContentText("Missing title!");
                    break;
                case "de":
                    alert.setContentText("Fehlender Titel!");
                    break;
                case "fr":
                    alert.setContentText("Titre manquant!");
                    break;
                case "nl":
                    alert.setContentText("Ontbrekende titel!");
                    break;
            }
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//////////
            String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
            switch (l) {
                case "en":
                    alert.setTitle("Change title");
                    alert.setContentText("Are you sure you want to change the title of this event!?");
                    alert.getButtonTypes()
                            .setAll(new ButtonType("Yes", ButtonBar.ButtonData.YES),
                                    new ButtonType("No", ButtonBar.ButtonData.NO));
                    break;
                case "de":
                    alert.setTitle("Titel ändern");
                    alert.setContentText("Sind Sie sicher, dass Sie den Titel dieser Veranstaltung ändern möchten!?");
                    alert.getButtonTypes()
                            .setAll(new ButtonType("Ja", ButtonBar.ButtonData.YES),
                                    new ButtonType("Nein", ButtonBar.ButtonData.NO));
                    break;
                case "fr":
                    alert.setTitle("Changer le titre");
                    alert.setContentText("Êtes-vous sûr de vouloir changer le titre de cet événement!?");
                    alert.getButtonTypes()
                            .setAll(new ButtonType("Oui", ButtonBar.ButtonData.YES),
                                    new ButtonType("Non", ButtonBar.ButtonData.NO));
                    break;
                case "nl":
                    alert.setTitle("Titel wijzigen");
                    alert.setContentText("Weet je zeker dat je de titel van dit evenement wilt veranderen!?");
                    alert.getButtonTypes()
                            .setAll(new ButtonType("Bevestigend", ButtonBar.ButtonData.YES),
                                    new ButtonType("Geen", ButtonBar.ButtonData.NO));
                    break;
            }
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType.getButtonData().equals(ButtonBar.ButtonData.YES)) {
                    Event updated = new Event(changeTitleField.getText());
                    updated.setId(event.getId());
                    try{
                        this.event = server.editEventTitle(updated);
                        eventTitle.setText(this.event.getTitle());
                        changeTitleField.clear();
                    } catch(Exception e){
                        Alert alert1 = new Alert(Alert.AlertType.ERROR);
                        alert1.initModality(Modality.APPLICATION_MODAL);
                        alert1.setContentText("Could set title!");
                        alert1.showAndWait();
                    }
                }
            });
        }
    }

    public static class TableRow {
        private String date;
        private String description;
        private String participants;
        @FXML
        private TextFlow tags;
        @FXML
        private Button editButton;
        @FXML
        private Button deleteButton;

        public TableRow(String date, String description, String participants, TextFlow tags, Button editButton, Button deleteButton) {
            this.date = date;
            this.description = description;
                this.participants = participants;
            this.tags = tags;
            this.editButton = editButton;
            this.deleteButton = deleteButton;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getParticipants() {
            return participants;
        }

        public void setParticipants(String participants) {
            this.participants = participants;
        }

        public TextFlow getTags(){
            return tags;
        }

        public void setTags(TextFlow tags){
            this.tags = tags;
        }

        public Button getEditButton() {
            return editButton   ;
        }

        public void setEditButton(Button editButton) {
            this.editButton = editButton;
        }
        public Button getDeleteButton() {
            return deleteButton   ;
        }

        public void setDeleteButton(Button deleteButton) {
            this.deleteButton = deleteButton;
        }

    }

    public ComboBox<String> getParticipantsList() {
        return participantsList;
    }

    public Button getFromPersonBtn() {
        return fromPersonBtn;
    }

    public Button getIncludingPersonBtn() {
        return includingPersonBtn;
    }

    public Button getParticipantAdd() {
        return participantAdd;
    }

    public Button getSendInvite() {
        return sendInvite;
    }

    public Button getAddExpense() {
        return addExpense;
    }

    public Button getAllTable() {
        return allTable;
    }

    public Button getSettleDebts() {
        return settleDebts;
    }

    public Button getBackBtn() {
        return backBtn;
    }

    public Pane getRoot() {
        return root;
    }

    public Button getChangeTitleBtn() {
        return changeTitleBtn;
    }

    public TableView<TableRow> getExpenseTable() {
        return expenseTable;
    }
}

