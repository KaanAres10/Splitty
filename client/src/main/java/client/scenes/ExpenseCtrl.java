package client.scenes;

import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class ExpenseCtrl extends Application {

    @FXML
    TableView<Tag> eventTagsTable;
    @FXML
    private TableColumn<Tag, String> eventTagNameColumn;
    @FXML
    TextField priceField;

    @FXML
    TextField description;

    @FXML
    ListView manualAddField;

    @FXML
    RadioButton manualAdd;

    @FXML
    private Rectangle rectangle;
    @FXML
    private Label noLabel;

    @FXML
    Label manualLabel;

    @FXML
    ColorPicker colorPicker;

    @FXML
    private TextField addParticipant;

    @FXML
    private Button addParticipantBtn;

    @FXML
    Button addTagBtn;

    @FXML
    RadioButton allAdd;

    @FXML
    Button confirm;

    @FXML
    Button cancel;

    @FXML
    ListView expenseTags;

    @FXML
    ComboBox<String> expensePayer;

    @FXML
    TextField manualTag;

    @FXML
    DatePicker expenseDate;

    @FXML
    ComboBox<String> expenseCurrency;

    @FXML
    Label tagLabel;

    @FXML
    Label selectLabel;

    ServerUtils server;

    MainCtrl mainCtrl;

    Event event;
    //
    Expense expense;

    User user;

    private EventHandler<ActionEvent> eventHandler;

    List<Participant> participants = new ArrayList<>();

    boolean isEditing = false;
    @FXML
    PieChart pieChart;
    List<Expense> expenses;
    @FXML
    private AnchorPane root;

    public ExpenseCtrl() {

    }

    @Inject
    public ExpenseCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        expenses = new ArrayList<>();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void loadExpensePage(User user, Event event, Expense expense) {
        this.user = user;
        this.event = event;
        this.expense = expense;
        if (event != null) this.participants = server.getParticipantsFromEvent(event.getId());
        priceField.clear();
        description.clear();
        expenseTags.getItems().clear();
        rectangle.setVisible(false);
        noLabel.setVisible(false);
        expenseTags.setCellFactory(param -> new ListCell<Tag>() {
            @Override
            protected void updateItem(Tag tag, boolean empty) {
                super.updateItem(tag, empty);
                if (empty || tag == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    // Create an HBox to hold the components
                    HBox hbox = new HBox();
                    hbox.setSpacing(1); // Set the spacing between elements

                    StackPane stackPane1 = new StackPane();
                    StackPane stackPane2 = new StackPane();
                    StackPane stackPane3 = new StackPane();
                    Label label = new Label(tag.getName());
                    label.setPrefWidth(88);

                    TextField textField = new TextField(tag.getName());
                    textField.setPrefWidth(88);
                    textField.setVisible(false); // Initially hidden

                    ColorPicker colorPicker = new ColorPicker();
                    colorPicker.setPrefWidth(38);
                    colorPicker.setVisible(false);

                    Button okButton = new Button("OK");
                    okButton.setVisible(false);

                    stackPane1.getChildren().addAll(label, textField);
                    hbox.getChildren().add(stackPane1);

                    Button editButton = new Button("");
                    Button deleteButton = new Button("");
                    editButton.getStyleClass().add("small-button");// Apply a CSS class for styling
                    ImageView imageView1 = new ImageView(
                        new Image(getClass().getResourceAsStream("/client/assets/edit.png")));
                    imageView1.setFitWidth(12); // Set the width you desire
                    imageView1.setFitHeight(14); // S
                    editButton.setGraphic(imageView1);
                    hbox.getChildren().add(editButton);

                    editButton.setOnAction(e -> {
                        label.setVisible(false);
                        textField.setVisible(true);
                        colorPicker.setVisible(true);
                        colorPicker.setValue(colorFromString(tag.getColor()));
                        okButton.setVisible(true);

                        editButton.setVisible(false);
                        deleteButton.setVisible(false);

                        textField.requestFocus();
                    });

                    stackPane2.getChildren().addAll(colorPicker, editButton);
                    hbox.getChildren().add(stackPane2);
                    deleteButton.getStyleClass()
                        .add("small-button"); // Apply a CSS class for styling
                    ImageView imageView2 = new ImageView(
                        new Image(getClass().getResourceAsStream("/client/assets/bin.png")));
                    imageView2.setFitWidth(12); // Set the width you desire
                    imageView2.setFitHeight(14); // S
                    deleteButton.setGraphic(imageView2);
                    stackPane3.getChildren().addAll(deleteButton, okButton);
                    hbox.getChildren().add(stackPane3);
                    deleteButton.setOnAction(e -> {
                        Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                        String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
                        switch (l) {
                            case "en":
                                alertConfirmation.setTitle("Confirmation Dialog");
                                alertConfirmation.setHeaderText("Delete Confirmation");
                                alertConfirmation.setContentText(
                                    "Are you sure you want to delete the Tag?");
                                alertConfirmation.getButtonTypes()
                                    .setAll(new ButtonType("Yes", ButtonBar.ButtonData.YES),
                                        new ButtonType("No", ButtonBar.ButtonData.NO));
                                break;
                            case "de":
                                alertConfirmation.setTitle("Bestätigung");
                                alertConfirmation.setHeaderText("Löschen Bestätigung");
                                alertConfirmation.setContentText(
                                    "Sind Sie sicher, dass Sie das Tag löschen möchten?");
                                alertConfirmation.getButtonTypes()
                                    .setAll(new ButtonType("Ja", ButtonBar.ButtonData.YES),
                                        new ButtonType("Nein", ButtonBar.ButtonData.NO));
                                break;
                            case "fr":
                                alertConfirmation.setTitle("Dialogue de Confirmation");
                                alertConfirmation.setHeaderText("Confirmation de Suppression");
                                alertConfirmation.setContentText(
                                    "Etes-vous sûr de vouloir supprimer le tag ?");
                                alertConfirmation.getButtonTypes()
                                    .setAll(new ButtonType("Oui", ButtonBar.ButtonData.YES),
                                        new ButtonType("Non", ButtonBar.ButtonData.NO));
                                break;
                            case "nl":
                                alertConfirmation.setTitle("Bevestigingsvenster");
                                alertConfirmation.setHeaderText("Bevestiging verwijderen");
                                alertConfirmation.setContentText(
                                    "Weet u zeker dat u de tag wilt verwijderen?");
                                alertConfirmation.getButtonTypes()
                                    .setAll(new ButtonType("Ja", ButtonBar.ButtonData.YES),
                                        new ButtonType("Nee", ButtonBar.ButtonData.NO));
                                break;
                        }

                        boolean flagOk = false;
                        alertConfirmation.showAndWait().ifPresent(response2 -> {
                            if (response2.getButtonData().equals(ButtonBar.ButtonData.YES)) {
                                List<Tag> tags = expenseTags.getItems();
                                for (Tag t : tags) {
                                    if (Objects.equals(t.getName(), label.getText())) {
                                        expenseTags.getItems().remove(t);
                                        /////
                                        setTable();
                                        rebuildTagsList();
                                        return;
                                    }
                                }
                            }
                        });
                    });

                    okButton.setOnAction(e -> {
                        //we should change the color and rename the tag
                        String newName = textField.getText();
                        Color color = colorPicker.getValue();
                        List<Tag> tags = expenseTags.getItems();
                        for (Tag t : tags) {
                            if (Objects.equals(t.getName(), label.getText()) &&
                                !Objects.equals(tag, t)) {
                                Alert alert = new Alert(Alert.AlertType.WARNING);

                                alert.setTitle("Tag Already Exists");
                                alert.setHeaderText(null);
                                alert.setContentText(
                                    "The tag already exists. Please choose a different tag name " +
                                        "or select it from suggested Tags!");
                                alert.showAndWait();
                                return;
                            }
                        }
                        tag.setName(newName);
                        tag.setColor(color.toString());
                        label.setVisible(true);
                        textField.setVisible(false);
                        colorPicker.setVisible(false);
                        okButton.setVisible(false);
                        editButton.setVisible(true);
                        deleteButton.setVisible(true);
                        textField.requestFocus();

                        expenseTags.refresh();
                    });

                    setStyle("-fx-background-color: " + toCssColor(tag.getColor()) + ";");

                    setGraphic(hbox);
                }
            }
        });
        if (expense != null) {
            List<Tag> tags = expense.getTags();
            for (Tag t : tags) {
                addTagToList(t);
            }
        }
        expensePayer.getItems().clear();
        expensePayer.setValue("");
        expensePayer.setOnAction(e -> initSelectParticipant());
        expenseCurrency.getItems().clear();
        initialize();
        if (expense != null) {
            this.expense = expense;
            isEditing = true;
            loadCurrentExpense(expense);
            String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
            switch (l){
                case "en":
                    confirm.setText("Edit");
                    break;
                case "de":
                    confirm.setText("Bearbeiten");
                    break;
                case "fr":
                    confirm.setText("Éditer");
                    break;
                case "nl":
                    confirm.setText("Bewerken");
                    break;
            }
        } else {
            String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
            switch (l) {
                case "en":
                    confirm.setText("Add");
                    break;
                case "de":
                    confirm.setText("Hinzufügen");
                    break;
                case "fr":
                    confirm.setText("Ajouter");
                    break;
                case "nl":
                    confirm.setText("Toevoegen");
                    break;
            }
        }
        setTable();

        eventTagsTable.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1 && !eventTagsTable.getSelectionModel().isEmpty()) {
                Tag selectedTag = eventTagsTable.getSelectionModel().getSelectedItem();
                addTagToList(selectedTag);
                setTable();
            }
        });
    }

    private void setTable() {
        eventTagsTable.getItems().clear();
        eventTagsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        List<Tag> tags = server.getAllTagsFromEvent(event.getId());
        List<Tag> tagsInTheTable = new LinkedList<>();
        //if a tag is currently in the list, we do not add it to the table
        for (Tag t : tags) {
            boolean flagAlredyAdded = false;

            List<Tag> tags2 = expenseTags.getItems();
            for(Tag t2: tags2){
                if(Objects.equals(t.getName(), t2.getName())){
                    flagAlredyAdded = true;
                    break;
                }
            }
            if (!flagAlredyAdded) {
                tagsInTheTable.add(t);
            }
        }

        ObservableList<Tag> observableTags = FXCollections.observableList(tagsInTheTable);
        if (!tagsInTheTable.isEmpty()) {
            eventTagsTable.getItems().addAll(observableTags);
            rectangle.setVisible(false);
            noLabel.setVisible(false);
        } else {
            rectangle.setVisible(true);
            noLabel.setVisible(true);
            String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
            switch (l){
                case "en":
                    noLabel.setText("No Available Tags");
                    break;
                case "de":
                    noLabel.setText("Keine verfügbaren Tags");
                    break;
                case "fr":
                    noLabel.setText("Pas D'Étiquettes Disponibles");
                    break;
                case "nl":
                    noLabel.setText("Geen Beschikbare Tags");
                    break;
            }

        }
        tableSetColors();
    }

    private void tableSetColors() {
        eventTagsTable.setRowFactory(tv -> {
            TableRow<Tag> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    row.setStyle("-fx-background-color: " + toCssColor(newItem.getColor()) + ";");
                } else {
                    row.setStyle("-fx-background-color: transparent;");
                }
            });
            return row;
        });
    }

    private boolean isOnThePage(List<Tag> tags, String name){
        List<Tag> tags3 = expenseTags.getItems();
        for (Tag t : tags3) {
            if (Objects.equals(t.getName(), name)) {
                return true;
            }
        }
        for (Tag t : tags) {
            if (Objects.equals(t.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    private void rebuildTagsList() {
        List<Tag> tags = new ArrayList<>();
        tags.addAll(expenseTags.getItems());
        expenseTags.getItems().clear();
        for (Tag t : tags) {
            expenseTags.getItems().add(t);
        }
        expenseTags.refresh();
    }

    private static URL getLocation(String path) {
        return ExpenseCtrl.class.getClassLoader().getResource(path);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var fxml = new FXMLLoader();
        fxml.setLocation(getLocation("client/scenes/Expense.fxml"));
        var scene = new Scene(fxml.load());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    void backBtn() {
        isEditing = false;
        mainCtrl.showEventOverview(user, event);
    }

    public void initialize() {
        for (Participant p : participants) {
            expensePayer.getItems().add(p.getName());
        }
        initSelectParticipant();
        expenseCurrency.getItems().addAll("\u20AC", "$", "\u00A3");
        expenseCurrency.setValue("\u20AC");
        expenseCurrency.setOnAction(e -> switchCurrency());

        expenseTags.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        manualAddField.setDisable(!manualAdd.isSelected());
        manualAddField.setVisible(!allAdd.isSelected());
        manualLabel.setVisible(!allAdd.isSelected());
        initializeShortcuts();
        // Call a method to update the pie chart with expense data
//       updatePieChart();
    }

    private void initializeShortcuts() {
        // Existing event filters
        root.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case ENTER:
                    break;
                case ESCAPE:
                    backBtn();
                    break;
            }
        });
        expensePayer.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DOWN) {
                description.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                cancel.requestFocus();
            }
        });
        description.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DOWN) {
                priceField.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                expensePayer.requestFocus();
            }
        });
        priceField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DOWN) {
                expenseDate.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                description.requestFocus();
            }
        });
        expenseDate.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DOWN) {
                expenseTags.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                priceField.requestFocus();
            }
        });
        expenseTags.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DOWN) {
                manualTag.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                expenseDate.requestFocus();
            }
        });
        manualTag.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DOWN) {
                addTagBtn.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                expenseTags.requestFocus();
            }
        });
        addTagBtn.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addTag();
            } else if (event.getCode() == KeyCode.UP) {
                manualTag.requestFocus();
            } else if (event.getCode() == KeyCode.DOWN) {
                colorPicker.requestFocus();
            }
        });
        colorPicker.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DOWN) {
                confirm.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                addTagBtn.requestFocus();
            }
        });
        confirm.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                editCreate();
            } else if (event.getCode() == KeyCode.UP) {
                addTagBtn.requestFocus();
            } else if (event.getCode() == KeyCode.DOWN) {
                cancel.requestFocus();
            }
        });
        cancel.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                backBtn();
            } else if (event.getCode() == KeyCode.UP) {
                confirm.requestFocus();
            }
        });
    }

    void updatePieChart() {
        if (pieChart == null) {
            System.err.println("PieChart instance is null. Cannot update.");
            return;
        }

        if (expenses == null || expenses.isEmpty()) {
            pieChart.setData(FXCollections.emptyObservableList());
            return;
        }

        // Collect
        List<String> allTags = new ArrayList<>();
        for (Expense expense : expenses) {
            for (Tag tag : expense.getTags()) {
                allTags.add(tag.getName());
            }
        }

        // Aggregate
        Map<String, Integer> tagCounts = new HashMap<>();
        for (String tag : allTags) {
            tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);
        }

        // Create
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : tagCounts.entrySet()) {
            PieChart.Data dataPoint = new PieChart.Data(entry.getKey(), entry.getValue());
            pieChartData.add(dataPoint);
        }

        // Update
        pieChart.setData(pieChartData);

    }

    public void initSelectParticipant() {
        manualAddField.getItems().clear();
        for (Participant p : participants) {
            if (!p.getName().equals(getSelectedPayer())) {
                CheckBox checkBox = new CheckBox();
                checkBox.setText(p.getName());
                manualAddField.getItems().add(checkBox);
            }
        }

    }

    void loadCurrentExpense(Expense expense) {
        expensePayer.setValue(expense.getPayer().getName());
        description.setText(expense.getTitle());
        priceField.setText(expense.getAmount().toString());

        Date date = expense.getDate();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        expenseDate.setValue(localDate);
        expenseCurrency.setValue(expense.getCurrency());
        initSelectParticipant();
    }

    public void updateUIWithBundle(ResourceBundle bundle) {
        expensePayer.setPromptText(bundle.getString("expensePayer"));
        description.setPromptText(bundle.getString("expenseDescription"));
        priceField.setPromptText(bundle.getString("expensePrice"));
        expenseDate.setPromptText(bundle.getString("expenseDate"));
        allAdd.setText(bundle.getString("everyone"));
        manualAdd.setText(bundle.getString("select"));
        manualLabel.setText(bundle.getString("selectParticipant"));
        manualTag.setPromptText(bundle.getString("addTags"));
        addTagBtn.setText(bundle.getString("addTags"));
        confirm.setText(bundle.getString("editCreate"));
        cancel.setText(bundle.getString("cancel"));
        expenseCurrency.setPromptText(bundle.getString("currency"));
        tagLabel.setText(bundle.getString("expenseTag"));
        selectLabel.setText(bundle.getString("selectLabel"));
    }

    public void switchCurrency() {
        String selectedCurrency = expenseCurrency.getValue();

        switch (selectedCurrency) {
            case "\u20AC" -> {
                // Convert with € value
            }
            case "$" -> {
                // Convert with $ value
            }
            case "\u00A3" -> {
                // Convert with £ value
            }
        }
    }

    @FXML
    public void addTag() {
        //add a new tag to a list
        String newTagName = manualTag.getText();
        if (!newTagName.isEmpty() && !alreadyExists(newTagName)) {
            HashSet<Expense> set = new HashSet<>();
            Tag newTag = new Tag(newTagName, colorPicker.getValue().toString(), set, event);
            manualTag.clear();
            addTagToList(newTag);
        }
    }

    @FXML
    public void selectColor(ActionEvent actionEvent) {
        Color selectedColor = colorPicker.getValue();
        System.out.println("Selected Color: " + selectedColor.toString());
    }

    public boolean alreadyExists(String tagName) {
        List<Tag> tags = server.getAllTagsFromEvent(event.getId());
        for (Tag t : tags) {
            if (Objects.equals(t.getName(), tagName)) {
                // if this tag already exists
                Alert alert = new Alert(Alert.AlertType.WARNING);

                alert.setTitle("Tag Already Exists");
                alert.setHeaderText(null);
                alert.setContentText("The tag already exists. Please choose a different tag name " +
                    "or select it from suggested Tags!");
                alert.showAndWait();

                //such tag already exists!
                manualTag.clear();
                return true;
            }
        }

        List<Tag> tags4 = expenseTags.getItems();
        for(Tag t: tags4){
            if(Objects.equals(t.getName(), tagName)){
                // if this tag already exists
                Alert alert = new Alert(Alert.AlertType.WARNING);

                alert.setTitle("Tag Already Exists");
                alert.setHeaderText(null);
                alert.setContentText(
                    "The tag is already added to current Expense. Please choose a different tag name");
                alert.showAndWait();

                //such tag already exists!
                manualTag.clear();
                return true;
            }
        }
        return false;
    }

    @FXML
    void handleToggle() {
        if (manualAdd.isSelected()) {
            manualAddField
                .setDisable(false);
            manualAddField
                .setVisible(true);
            manualLabel
                .setVisible(true);
        } else if (allAdd.isSelected()) {
            manualAddField
//                .setDisable(true);
                .setVisible(true);
            manualAddField
                .setVisible(false);
            manualLabel
                .setVisible(false);
        }
    }

    Participant findParticipantByName(String name, List<Participant> participants) {
        for (Participant participant : participants) {
            if (participant.getName().equals(name)) {
                return participant;
            }
        }
        return null;
    }

    @FXML
    public void editCreate() {
        Long id = null;
        if (isEditing) id = expense.getId();
        String payerName = expensePayer.getValue();
        if (payerName.isEmpty()) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Cannot add/edit an expense without a payer!");
            alert.showAndWait();
//            mainCtrl.showAddEdit(user,event,expense);
            return;
        }
        String title = description.getText();
        if (title.isEmpty()) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Cannot add/edit an expense without a description!");
            alert.showAndWait();
//            mainCtrl.showAddEdit(user,event,expense);
            return;
        }
        BigDecimal amount = new BigDecimal(0);
        try {
            amount = new BigDecimal(priceField.getText());
            if (amount.compareTo(new BigDecimal(0)) <= 0) throw new Exception();
        } catch (Exception e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Amount of money should be a positive number!");
            alert.showAndWait();
            return;
        }
        String currency = expenseCurrency.getValue();

        Date date = null;
        if (expenseDate.getValue() != null) {
            try {
                LocalDate localDate = expenseDate.getValue();
                LocalDateTime localDateTime = localDate.atStartOfDay();
                date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Cannot add/edit an expense without a date!");
            alert.showAndWait();
            return;
        }
        List<Tag> tags = new ArrayList<>(expenseTags.getItems());
        List<CheckBox> checkBoxes = manualAddField.getItems();
        List<Participant> selectedParticipants = new ArrayList<>();
        for (CheckBox ch : checkBoxes) {
            if (ch.isSelected())
                selectedParticipants.add(server.getParticipantByName(event.getId(), ch.getText()));
        }

        if (manualAdd.isSelected()) {
            if (selectedParticipants.isEmpty()) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText("No participants selected!");
                alert.showAndWait();
                return;
            }
            createExpense(id, findParticipantByName(payerName, participants),
                title, amount, currency, date, tags, selectedParticipants);
        } else {
            Participant payer = findParticipantByName(payerName, participants);
            participants.remove(payer);
            createExpense(id, payer, title, amount, currency, date, tags, participants);

        }

        mainCtrl.showEventOverview(user, event);
    }


    void createExpense(Long id, Participant payer, String title, BigDecimal amount,
                       String currency,
                       Date date, List<Tag> tags, List<Participant> participants) {
        this.expense = new Expense(event, title, amount, payer, date, currency, null, participants);

        expense.setId(id);
        if (allAdd.isSelected()) {
            expense.setParticipants(this.participants);
        }
        if (isEditing)
            expense = editExpense(expense);
        else
            expense = addExpense(expense);
        addTags(expense);
    }

    Expense addExpense(Expense expense) {
        try {
            if (expense.getAmount().compareTo(new BigDecimal(0)) <= 0) {
                throw new Exception("Amount should be more than 0!");
            }
            expense = server.addExpense(expense, event);
            expenses.add(expense);
        } catch (Exception e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage() + "\n check if all fields are filled");
            alert.showAndWait();
        }
        return expense;
    }


    Expense editExpense(Expense expense) {
        try {
            if (expense.getAmount().compareTo(new BigDecimal(0)) <= 0) {
                throw new Exception("Amount should be more than 0!");
            }
            expense = server.editExpense(expense, event);
            isEditing = false;
        } catch (Exception e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage() + "\n check if all fields are filled");
            alert.showAndWait();
        }

        return expense;
    }

    void addTags(Expense expense) {
        //delete deleted tags
        List<Tag> tagsInTheTable = eventTagsTable.getItems();
        for (Tag t : tagsInTheTable) {
            String name = t.getName();
            if (t.getExpenses() == null || t.getExpenses().isEmpty() ||
                (t.getExpenses().size() == 1 && t.getExpenses().contains(expense))) {
                if (t.getId() != null) {
                    String nameOfTag = t.getName();
                    if (!Objects.equals(nameOfTag, "Travel")
                        && !Objects.equals(nameOfTag, "Entrance Fees")
                        && !Objects.equals(nameOfTag, "Food"))
                        server.removeTag(event.getId(), t.getId());
                }
            } else {
                t.getExpenses().remove(expense);
                server.addTag(event.getId(), t);
            }
        }

        List<Tag> tags = expenseTags.getItems();
        expense.setTags(tags);
        expense = server.editExpense(expense, event);
    }

    private void addTagToList(Tag tag) {
        expenseTags.getItems().add(tag);
    }

    public static String toCssColor(String colorToString) {
        Color color = colorFromString(colorToString);

        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        return String.format("#%02X%02X%02X", red, green, blue);
    }

    static Color colorFromString(String colorString) {
        int red = Integer.parseInt(colorString.substring(2, 4), 16);
        int green = Integer.parseInt(colorString.substring(4, 6), 16);
        int blue = Integer.parseInt(colorString.substring(6, 8), 16);
        int alpha = Integer.parseInt(colorString.substring(8), 16);

        return Color.rgb(red, green, blue, alpha / 255.0);
    }

    private String getSelectedPayer() {
        return expensePayer.getValue();
    }
}