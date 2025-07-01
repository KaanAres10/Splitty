package client.scenes;


import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import commons.Tag;
import commons.User;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ContactDetailsCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    public Label nameLabel;
    @FXML
    public Label emailLabel;
    @FXML
    public Label ibanLabel;
    @FXML
    public Label bicLabel;
    @FXML
    public Label errorLabel;
    @FXML
    public TextField nameTextField;
    @FXML
    public TextField emailTextField;
    @FXML
    public TextField ibanTextField;
    @FXML
    public TextField bicTextField;
    @FXML
    public Button goBackButton;
    private Event event;
    private User user;
    boolean flagIsItEdit = false;
    boolean flagIsCreate = false;
    private boolean flagisJoin = false;
    private Participant editingParticipant;
    @FXML
    private Button okButton;
    @FXML
    private Label contactTitle;

    @FXML
    private Pane root;

    private Node focusedNode;


    @Inject
    public ContactDetailsCtrl(ServerUtils server, MainCtrl mainCtrl, Event event, User user) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.event = event;
        this.user = user;
    }

    //for "Adding" from Current Participants Page
    public void refresh(User user, Event event) {
        //if we pressed "Add"on the CurrentParticipants page
        this.user = user;
        this.event = event;
        flagIsCreate = false;
        flagIsItEdit = false;
        errorLabel.setVisible(false);
        String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
        switch (l){
            case "en":
                contactTitle.setText("Add Participant");
                break;
            case "de":
                contactTitle.setText("Teilnehmer hinzufügen");
                break;
            case "fr":
                contactTitle.setText("Ajouter un participant");
                break;
            case "nl":
                contactTitle.setText("Deelnemer toevoegen");
                break;
        }
    }

    //for "Editing" from Current Participants Page
    public void refresh(Participant participant) {
        //if we pressed "Edit" on the CurrentParticipants page
        this.user = participant.getUser();
        this.event = participant.getEvent();
        flagIsCreate = false;
        flagIsItEdit = true;
        errorLabel.setVisible(false);
        editingParticipant = participant;
        nameTextField.setText(participant.getName());
        emailTextField.setText(participant.getMail());
        ibanTextField.setText(participant.getIban());
        bicTextField.setText(participant.getBic());
        String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
        switch (l){
            case "en":
                contactTitle.setText("Edit Participant");
                break;
            case "de":
                contactTitle.setText("Teilnehmer bearbeiten");
                break;
            case "fr":
                contactTitle.setText("Modifier le participant");
                break;
            case "nl":
                contactTitle.setText("Deelnemer bewerken");
                break;
        }
    }

    // for "Creating" an event
    public void refresh(User user, Event event, boolean flagIsCreate) {
        this.user = user;
        this.event = event;
        if (flagIsCreate)
            this.flagIsCreate = true;
        else
            this.flagisJoin = true;
        errorLabel.setVisible(false);
        String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
        switch (l){
            case "en":
                contactTitle.setText("Enter your details");
                break;
            case "de":
                contactTitle.setText("Geben Sie Ihre Details ein");
                break;
            case "fr":
                contactTitle.setText("Entrez vos coordonnées");
                break;
            case "nl":
                contactTitle.setText("Voer uw gegevens in");
                break;
        }
        contactTitle.setText("Enter your details");
        nameTextField.setPromptText("Name");
        emailTextField.setPromptText("Email");
        ibanTextField.setPromptText("IBAN");
        bicTextField.setPromptText("BIC");
    }

    @FXML
    public void cancelButtonPressed() {
        //should clear all fields and return back to Current Participants page
        clearFields();
        if (!flagIsCreate && !flagisJoin) {
            mainCtrl.showCurrentParticipants(user, event);
        } else {
            mainCtrl.showStartScreen();
        }
    }

    @FXML
    public void okButtonPressed() {
        if (nameTextField.getText().isEmpty() || emailTextField.getText().isEmpty()) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
            switch(l){
                case "en":
                    alert.setContentText("Please fill in your name and email");
                    break;
                case "de":
                    alert.setContentText("Bitte geben Sie Ihren Namen und Ihre E-Mail-Adresse ein");
                    break;
                case "fr":
                    alert.setContentText("Merci de renseigner votre nom et votre email");
                    break;
                case "nl":
                    alert.setContentText("Vul uw naam en e-mailadres in");
            }

            alert.showAndWait();
            return;
        }
        if (flagIsCreate) {
            handleCreate();
            return;
        }
        if (flagisJoin) {
            handleJoin();
            return;
        }
        // we need to add/edit participant
        Participant participant;
        if (!flagIsItEdit) {
            // we are creating a new participant here
            participant = getParticipant();
            if (checkCorrectnessOfName(participant)) {
                try {
                    participant = server.addParticipant(participant);
                } catch (WebApplicationException e) {

                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                    return;
                }
                clearFields();
                mainCtrl.showCurrentParticipants(user, event);
            }
        } else {
            // if this is editing page, then we are editing already existing participant
            participant = editingParticipant;
            participant.setName(nameTextField.getText());
            participant.setMail(emailTextField.getText());
            participant.setIban(ibanTextField.getText());
            participant.setBic(bicTextField.getText());
            if (checkCorrectnessOfName(participant)) {
                try {
                    participant = server.updateParticipant(participant);
                } catch (WebApplicationException e) {

                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                    return;
                }
                clearFields();
                mainCtrl.showCurrentParticipants(user, event);
            }
        }
        mainCtrl.showCurrentParticipants(user, event);
        goBackButton.requestFocus();
        clearFields();
    }

    public void handleCreate() {
        Participant participant = getParticipant();
        try {
            event = server.addEvent(event, user);
            participant.setEvent(event);
            participant.setOwner(true);
            participant = server.addParticipant(participant);

            Tag food = new Tag("Food", Color.rgb(144, 238, 144).toString(), null, event);
            server.addTag(event.getId(), food);
            Tag eFees = new Tag("Entrance Fees", Color.rgb(30, 144, 255).toString(), null, event);
            server.addTag(event.getId(), eFees);
            Tag travel = new Tag("Travel", Color.rgb(255, 105, 140).toString(), null, event);
            server.addTag(event.getId(), travel);

            mainCtrl.showEventOverview(user, event);
        } catch (Exception e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            System.out.println(e.getMessage());
            alert.showAndWait();
        }
        clearFields();
    }

    public void handleJoin() {
        Participant participant = getParticipant();
        try {
            participant = server.addParticipant(participant);
            mainCtrl.showEventOverview(user, event);
        } catch (Exception e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            System.out.println(e.getMessage());
            alert.showAndWait();
        }
        clearFields();
    }

    public boolean checkCorrectnessOfName(Participant participant) {
        boolean uniqueName = true, emptyName = false;
        if (Objects.equals(participant.getName(), "No info") || Objects.equals(participant.getName(), "")) {
            fillErrorLabel(false, participant);
            emptyName = true;
        }
        // need to check if there is no such name or email in the db
        List<Participant> participantList = server.getParticipantsFromEvent(event.getId());
        for (Participant participantInList : participantList) {
            if (Objects.equals(participantInList.getId(), participant.getId())) {
                continue;
            }
            if (Objects.equals(participant.getName(), participantInList.getName())) {
                uniqueName = false;
                break;
            }
        }
        if (!uniqueName) {
            fillErrorLabel(true, participant);
        }
        return uniqueName && !emptyName;
    }

    public void fillErrorLabel(boolean typeOfError, Participant participant) {
        //false - empty
        // true - not unque
        String s = "";
        if (typeOfError) {
            s += "Name: '" + participant.getName() + "' already exists. \n";
        } else {
            s += "Name can not be empty!\n";
        }
        errorLabel.setText(s);
        errorLabel.setVisible(true);
    }

    public void clearFields() {
        nameTextField.clear();
        emailTextField.clear();
        ibanTextField.clear();
        bicTextField.clear();
    }

    public Participant getParticipant() {
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        String iban = ibanTextField.getText();
        String bic = bicTextField.getText();
        if (Objects.equals(name, ""))
            name = "No info";
        if (Objects.equals(email, ""))
            email = "No info";
        if (Objects.equals(iban, ""))
            iban = "No info";
        if (Objects.equals(bic, ""))
            bic = "No info";
        return new Participant(user, event, name, iban, bic, email);
    }

    public void updateUIWithBundle(ResourceBundle bundle) {
        contactTitle.setText(bundle.getString("contactTitle"));
        nameLabel.setText(bundle.getString("nameLabel"));
        nameTextField.setPromptText(bundle.getString("nameTextField"));
        emailLabel.setText(bundle.getString("emailLabel"));
        emailTextField.setPromptText(bundle.getString("emailTextField"));
        ibanLabel.setText(bundle.getString("ibanLabel"));
        ibanTextField.setPromptText(bundle.getString("ibanTextField"));
        bicLabel.setText(bundle.getString("bicLabel"));
        bicTextField.setPromptText(bundle.getString("bicTextField"));
        goBackButton.setText(bundle.getString("abortButton"));
        okButton.setText(bundle.getString("okButton"));
        errorLabel.setText(bundle.getString("errorLabel"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeShortcuts();
    }


    private void initializeShortcuts() {
        okButton.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                okButtonPressed();
            }
        });
        root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            switch (e.getCode()) {
                case UP:
                    focusPreviousField();
                    break;
                case DOWN:
                    focusNextField();
                    break;
                case ESCAPE:
                    cancelButtonPressed();
                    break;
            }
        });

        addFocusListeners();
    }

    //add focus listeners to all interactive nodes
    private void addFocusListeners() {
        nameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                focusedNode = nameTextField;
            }
        });
        emailTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                focusedNode = emailTextField;
            }
        });
        ibanTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                focusedNode = ibanTextField;
            }
        });
        bicTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                focusedNode = bicTextField;
            }
        });

        goBackButton.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                focusedNode = goBackButton;
            }
        });
        okButton.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                focusedNode = okButton;
            }
        });
    }

    // focus on the previous field
    private void focusPreviousField() {
        if (focusedNode == null) return;

        if (focusedNode == nameTextField) {
            bicTextField.requestFocus();
        } else if (focusedNode == emailTextField) {
            nameTextField.requestFocus();
        } else if (focusedNode == ibanTextField) {
            emailTextField.requestFocus();
        } else if (focusedNode == bicTextField) {
            ibanTextField.requestFocus();
        }
    }

    // focus on the next field
    private void focusNextField() {
        if (focusedNode == null) return;

        if (focusedNode == nameTextField) {
            emailTextField.requestFocus();
        } else if (focusedNode == emailTextField) {
            ibanTextField.requestFocus();
        } else if (focusedNode == ibanTextField) {
            bicTextField.requestFocus();
        } else if (focusedNode == bicTextField) {
            goBackButton.requestFocus();
        } else if (focusedNode == goBackButton) {
            okButton.requestFocus();
        }
    }

}

