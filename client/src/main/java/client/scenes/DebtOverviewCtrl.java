package client.scenes;

import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Debt;
import commons.Event;
import commons.User;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class DebtOverviewCtrl {
    private final MainCtrl mainCtrl;
    private final ServerUtils serverUtils;
    private User user;
    private Event event;
    private List<Debt> debts; // store debts locally
    private Method lastUsedFilter;

    private Method displaySum;

    @FXML
    private Label sumLabel1;
    @FXML
    private Label titleLabel;
    @FXML
    private Button refreshButton;
    @FXML
    private Button backButton;
    @FXML
    private CheckBox includeCheckbox;
    @FXML
    private Button allDebtButton;
    @FXML
    private Button yourDebtButton;
    @FXML
    private Button yourCreditButton;
    @FXML
    private TableView<DebtListEntry> debtTableView;
    @FXML
    private TableColumn<DebtListEntry, Button> expandColumn;
    @FXML
    private TableColumn<DebtListEntry, VBox> infoColumn;
    @FXML
    private TableColumn<DebtListEntry, Button> resolveColumn;
    @FXML
    private TableView<SumOverview> sumTable;
    @FXML
    private TableColumn<SumOverview, String> nameColumn;
    @FXML
    private TableColumn<SumOverview, String> giveColumn;
    @FXML
    private TableColumn<SumOverview, String> receiveColumn;
    @FXML
    private TableColumn<SumOverview, String> shareColumn;
    @FXML
    private Label sumLabel;
    private Map<String, Pair<BigDecimal, BigDecimal>> map;


    @Inject
    public DebtOverviewCtrl(MainCtrl mainCtrl, ServerUtils serverUtils) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
    }

    public void initialize() {
        includeCheckbox.setOnAction(event -> {
            // changes whether already settled debts should be included so rerender tableview
            try {
                lastUsedFilter.invoke(this);
                displaySum.invoke(this);
                System.out.println(map);

            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });

        // todo: Fix language of buttons

        expandColumn.setCellValueFactory(this::expandButtonCellValueFactory);

        infoColumn.setCellValueFactory(this::infoCellValueFactory);

        resolveColumn.setCellValueFactory(this::resolveButtonCellValueFactory);

        nameColumn.setCellValueFactory(
            new PropertyValueFactory<DebtOverviewCtrl.SumOverview, String>("name"));
        giveColumn.setCellValueFactory(
            new PropertyValueFactory<DebtOverviewCtrl.SumOverview, String>("give"));
        receiveColumn.setCellValueFactory(
            new PropertyValueFactory<DebtOverviewCtrl.SumOverview, String>("receive"));
        shareColumn.setCellValueFactory(
            new PropertyValueFactory<DebtOverviewCtrl.SumOverview, String>("share"));
    }

    private SimpleObjectProperty<Button> resolveButtonCellValueFactory(
        TableColumn.CellDataFeatures<DebtListEntry, Button> param) {
        if (!param.getValue().getReceiver().getUser().equals(user)) {
            // don't allow a user to mark someone else's debt as resolved or unresolved
            Button noButton = new Button();
            noButton.setVisible(false);
            return new SimpleObjectProperty<>(noButton);
        }
        if (param.getValue().getPaid()) {
            // if already marked as paid allow creditor to mark as unresolved
            Button markNotReceived = new Button("❌");
            Tooltip tooltip = new Tooltip("Mark Debt Not Resolved.");
            Tooltip.install(markNotReceived, tooltip);
            markNotReceived.setAlignment(Pos.CENTER);
            markNotReceived.getStyleClass().add("debt-unresolve-button");
            markNotReceived.setOnAction(event -> {
                serverUtils.markDebtAs(param.getValue(), false);
                markNotReceived.setDisable(true);
            });
            return new SimpleObjectProperty<>(markNotReceived);
        } else {
            // user is the creditor, and it hasn't been paid yet
            Button markReceived = new Button("✅");
            Tooltip tooltip = new Tooltip("Mark Debt Resolved.");
            Tooltip.install(markReceived, tooltip);
            markReceived.setAlignment(Pos.CENTER);
            markReceived.getStyleClass().add("debt-resolve-button");
            markReceived.setOnAction(event -> {
                serverUtils.markDebtAs(param.getValue(), true);
                markReceived.setDisable(true);
            });
            return new SimpleObjectProperty<>(markReceived);
        }
    }

    private SimpleObjectProperty<VBox> infoCellValueFactory(
        TableColumn.CellDataFeatures<DebtListEntry, VBox> param) {
        // This doesn't show the text in the minimum amount of space, but I can't think of a better approach
        TextArea summary = new TextArea(getDebtSummary(param.getValue()));
        TextArea contactInfo = new TextArea(getContactText(param.getValue()));
        summary.setEditable(false);
        contactInfo.setEditable(false);
        summary.setWrapText(true);
        contactInfo.setWrapText(true);
        summary.setPrefRowCount(summary.getParagraphs().size());
        contactInfo.setPrefRowCount(contactInfo.getParagraphs().size());

        if (!param.getValue().isShowContact()) {
            contactInfo.getStyleClass().add("display-none"); // hide
        }
        return new SimpleObjectProperty<>(new VBox(summary, contactInfo));
    }

    private SimpleObjectProperty<Button> expandButtonCellValueFactory(
        TableColumn.CellDataFeatures<DebtListEntry, Button> param) {
        Button expand = new Button(param.getValue().expandChar);
        Tooltip tooltip = new Tooltip("Expand contact info.");
        Tooltip.install(expand, tooltip);
        expand.setAlignment(Pos.CENTER);
        expand.setOnAction(event -> {
            boolean visible = param.getValue().isShowContact();
            param.getValue().setShowContact(!visible);
            String expandChar = param.getValue().expandChar;
            param.getValue().expandChar = expandChar.equals(">") ? "v" : ">";
            debtTableView.refresh();
        });
        return new SimpleObjectProperty<>(expand);
    }

    public void refresh(User user, Event event) {
        this.user = user;
        this.event = event;

        serverUtils.registerForDataWebSocket("/topic/" + event.getId() + "/debts", Debt.class,
            debt -> {
                for (int i = 0; i < debts.size(); i++) {
                    // If debt is already stored then replace it
                    if (debt.getId().equals(debts.get(i).getId())) {
                        debts.set(i, debt);
                        // shouldn't run on the websocket, but the javaFX thread
                        Platform.runLater(() -> {
                            try {
                                lastUsedFilter.invoke(this);
                                displaySum.invoke(this);
                                System.out.println(map);

                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        return;
                    }
                }
                // Completely new debt entity
                debts.add(debt);
                Platform.runLater(() -> {
                    try {
                        lastUsedFilter.invoke(this);
                        displaySum.invoke(this);
                        System.out.println(map);

                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
            });

        serverUtils.registerForDataWebSocket("/topic/" + event.getId() + "/debts/delete",
            Debt.class, debt -> {
                try {
                    Field debtID = Debt.class.getDeclaredField("id");
                    // shouldn't run on the websocket, but the javaFX thread
                    Platform.runLater(() -> {
                        removeFromDebts(debt, debtID);
                    });
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            });

        getDataFromServer();
        try {
            lastUsedFilter = DebtOverviewCtrl.class.getMethod("displayAllDebt");
            lastUsedFilter.invoke(this);
            displaySum = DebtOverviewCtrl.class.getMethod("displaySumDebts");
            displaySum.invoke(this);
            System.out.println(map);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    // Removes debt from all necessary storage location if there's
    // a debt object that has the same values as debt for all fields.
    public void removeFromDebts(Debt debt, Field... fields) {
        debts.removeIf(debtInList -> {
            for (Field field : fields) {
                try {
                    // if debt doesn't have every field the same, leave it in the list
                    field.setAccessible(true);
                    var val1 = field.get(debt);
                    var val2 = field.get(debtInList);
                    if (!val1.equals(val2)) return false;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        });

        // invoke last used filter
        try {
            lastUsedFilter.invoke(this);
            displaySum.invoke((this));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void getDataFromServer() {
        debts = serverUtils.getEventDebt(event);
    }

    public void displayAllDebt() {
        List<DebtListEntry> mappedDebts = debts.stream()
            .filter(debt -> includeCheckbox.isSelected() || !debt.getPaid())
            .map(DebtListEntry::new)
            .toList();
        debtTableView.setItems(FXCollections.observableList(mappedDebts));

        try {
            lastUsedFilter = DebtOverviewCtrl.class.getMethod("displayAllDebt");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void displaySumDebts() {
        BigDecimal sum = new BigDecimal(0);
        map = new HashMap<>();
        for (Debt d : debts) {
            if (!d.getPaid()) {
                sum = sum.add(d.getAmount());
                if (map.containsKey(d.getReceiver().getName())) {
                    Pair<BigDecimal, BigDecimal> temp = map.get(d.getReceiver().getName());
                    BigDecimal toReceive= temp.getValue().add(d.getAmount());
                    Pair<BigDecimal, BigDecimal> other = new Pair<>(temp.getKey(),toReceive);
                    map.put(d.getReceiver().getName(), other);
                } else map.put(d.getReceiver().getName(), new Pair<>(new BigDecimal(0),d.getAmount()));
                if (map.containsKey(d.getParticipant().getName())) {
                    Pair<BigDecimal, BigDecimal> temp = map.get(d.getParticipant().getName());
                    BigDecimal toGive= temp.getKey().add(d.getAmount());
                    Pair<BigDecimal, BigDecimal> other = new Pair<>(toGive,temp.getValue());
                    map.put(d.getParticipant().getName(), other);
                } else map.put(d.getParticipant().getName(), new Pair<>(d.getAmount(),new BigDecimal(0)));
            }
        }
        sumLabel.setText(sum + "\u20AC");
        List<SumOverview> list = new ArrayList<>();
        for (String key : map.keySet()) {
            BigDecimal valueToGive = map.get(key).getKey();
            BigDecimal valueToReceive = map.get(key).getValue();
            if(valueToReceive == null) valueToReceive = new BigDecimal(0);
            if(valueToGive == null) valueToGive = new BigDecimal(0);

            BigDecimal share = valueToGive.divide(sum, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            list.add(new SumOverview(key, valueToGive.toString(), valueToReceive.toString(),
                share.toString()));
        }
        System.out.println(list.size());
        ObservableList<DebtOverviewCtrl.SumOverview> data = FXCollections.observableArrayList(list);
        sumTable.setItems(data);
    }


    public void displayYourDebt() {
        List<DebtListEntry> filteredDebts = debts.stream()
            .filter(debt -> debt.getParticipant().getUser().equals(user))
            .filter(debt -> includeCheckbox.isSelected() || !debt.getPaid())
            .map(DebtListEntry::new)
            .toList();
        debtTableView.setItems(FXCollections.observableList(filteredDebts));

        try {
            lastUsedFilter = DebtOverviewCtrl.class.getMethod("displayYourDebt");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void displayYourCredit() {
        List<DebtListEntry> filteredDebts = debts.stream()
            .filter(debt -> debt.getReceiver().getUser().equals(user))
            .filter(debt -> includeCheckbox.isSelected() || !debt.getPaid())
            .map(DebtListEntry::new)
            .toList();
        debtTableView.setItems(FXCollections.observableList(filteredDebts));

        try {
            lastUsedFilter = DebtOverviewCtrl.class.getMethod("displayYourCredit");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private String getDebtSummary(Debt debt) {
        return debt.getParticipant().getName() + getLocalizedString(" owes ") +
            debt.getReceiver().getName() + ' ' +
            debt.getAmount() + ' ' +
            debt.getExpense().getCurrency();
    }

    private String getLocalizedString(String s){
        String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
        switch (l) {
            case "en":
                switch (s){
                    case "Bank information available, transfer money to\n":
                        return s;
                    case "No contact/payment information available":
                        return s;
                    case "Email configured: ":
                        return s;
                    case " owes ":
                        return s;
                }
            case "fr":
                switch (s){
                    case "Bank information available, transfer money to\n":
                        return "Informations bancaires disponibles, transférer de l'argent à\n";
                    case "No contact/payment information available":
                        return "Aucune information de contact/paiement disponible";
                    case "Email configured: ":
                        return "Email configuré : ";
                    case " owes ":
                        return " doit ";
                }
            case "de":
                switch (s){
                    case "Bank information available, transfer money to\n":
                        return "Bankinformationen verfügbar, Geld überweisen an\n";
                    case "No contact/payment information available":
                        return "Keine Kontakt-/Zahlungsinformationen verfügbar";
                    case "Email configured: ":
                        return "E-Mail konfiguriert: ";
                    case " owes ":
                        return " schuldet ";
                }
            case "nl":
                switch (s){
                    case "Bank information available, transfer money to\n":
                        return "Bankinformatie beschikbaar, geld overmaken naar\n";
                    case "No contact/payment information available":
                        return "Geen contact-/betalingsinformatie beschikbaar";
                    case "Email configured: ":
                        return "E-mail geconfigureerd: ";
                    case " owes ":
                        return " verschuldigd ";
                }
        }
        return s;
    }

    // temporary
    private String getContactText(Debt debt) {
        String out = "";
        if (debt.getReceiver().getIban() != null || debt.getReceiver().getBic() != null) {
            out += getLocalizedString("Bank information available, transfer money to\n");
        }
        if (debt.getReceiver().getIban() != null) {
            out += "Iban: " + debt.getReceiver().getIban() + '\n';
        }
        if (debt.getReceiver().getBic() != null) {
            out += "Bic: " + debt.getReceiver().getBic() + '\n';
        }
        if (debt.getParticipant().getMail() != null) {
            out += getLocalizedString("Email configured: ") + debt.getParticipant().getMail() + "\n";
        }
        return out.isEmpty() ? getLocalizedString("No contact/payment information available") : out;
    }

    public void goToEvent() {
        mainCtrl.showEventOverview(user, event);
    }

    public void refreshDebts() {
        try {
            getDataFromServer();
            lastUsedFilter.invoke(this);
            displaySum.invoke(this);
            System.out.println(map);

        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateUIWithBundle(ResourceBundle bundle) {
        titleLabel.setText(bundle.getString("titleLabel"));
        refreshButton.getTooltip().setText(bundle.getString("refreshButton"));
        allDebtButton.setText(bundle.getString("allDebtButton"));
        yourDebtButton.setText(bundle.getString("yourDebtButton"));
        yourCreditButton.setText(bundle.getString("yourCreditButton"));
        includeCheckbox.setText(bundle.getString("includeCheckbox"));
        nameColumn.setText(bundle.getString("name"));
        giveColumn.setText(bundle.getString("giveDebt"));
        receiveColumn.setText(bundle.getString("recieveDebt"));
        shareColumn.setText(bundle.getString("share"));
        sumLabel1.setText(bundle.getString("sumLabel"));
    }

    // A wrapper for the standard commons.Debt class
    // that stores information necessary for properly displaying entities in the table view
    private static class DebtListEntry extends Debt {
        private String expandChar = ">";
        private boolean showContact = false;

        public DebtListEntry(Debt debt) {
            super(
                debt.getId(),
                debt.getExpense(),
                debt.getParticipant(),
                debt.getAmount(),
                debt.getPaid(),
                debt.getReceiver()
            );
        }

        public boolean isShowContact() {
            return showContact;
        }

        public void setShowContact(boolean showContact) {
            this.showContact = showContact;
        }

        public String getExpandChar() {
            return expandChar;
        }

        public void setExpandChar(String expandChar) {
            this.expandChar = expandChar;
        }
    }

    public static class SumOverview {
        private String name;
        private String give;
        private String receive;
        private String share;

        public SumOverview(String name, String give, String receive, String share) {
            this.name = name;
            this.give = give;
            this.receive = receive;
            this.share = share;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGive() {
            return give;
        }

        public void setGive(String give) {
            this.give = give;
        }

        public String getReceive() {
            return receive;
        }

        public void setReceive(String receive) {
            this.receive = receive;
        }

        public String getShare() {
            return share;
        }

        public void setShare(String share) {
            this.share = share;
        }
    }
}
