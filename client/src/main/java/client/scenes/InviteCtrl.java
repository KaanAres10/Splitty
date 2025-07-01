package client.scenes;


import client.utils.ServerUtils;
import commons.Participant;

import client.utils.LanguageManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import commons.Event;
import client.utils.InviteUtils;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InviteCtrl {
    public ServerUtils server = new ServerUtils();
    private final InviteUtils inviteUtils = new InviteUtils();
    public Pane root;
    public Button copyButton;

    @FXML
    Text inviteTitle;

    @FXML
    TextField inviteField;
    @FXML
    Button inviteAdd;

    @FXML
    Button inviteButton;

    @FXML
    public Text inviteCode;

    @FXML
    ListView<String> emailList;

    @FXML
    private Text warningText;

    @FXML
    Text successText;
    @FXML
    Button removeButton;
    @FXML
    Button cancelButton;

    @FXML
    Text desc1;
    @FXML
    Text desc2;

    private final ObservableList<String> emails = FXCollections.observableArrayList();
    final Set<String> uniqueEmails = new HashSet<>();


    public Text getInviteCode() {
        return inviteCode;
    }

    public Text getSuccessText() {
        return successText;
    }
    public Event event;

    private boolean isGenerated = false;


    public void setEvent(Event event) {
        this.event = event;
    }
    @FXML
    public void initialize() {
        String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
        switch (l) {
            case "en":
                inviteTitle.setText("Invite Participants");
                inviteField.setPromptText("Enter email");
                break;
            case "de":
                inviteTitle.setText("Teilnehmer einladen");
                inviteField.setPromptText("E-Mail eingeben");
                break;
            case "fr":
                inviteTitle.setText("Inviter des participants");
                inviteField.setPromptText("Entrez l'email");
                break;
            case "nl":
                inviteTitle.setText("Deelnemers uitnodigen");
                inviteField.setPromptText("Voer e-mail in");
                break;
        }
        if (this.event != null) {
            if (this.event.getInviteCode() != null) isGenerated = true;
        }
        if (!isGenerated) {
            generateInviteCode();
            isGenerated = true;
        }else{
            inviteCode.setText(event.getInviteCode());
        }
        // Disable the invite button if the email list is empty
        if (!emailList.hasProperties()) {
            switch (l) {
                case "en":
                    emailList.setPlaceholder(new Text("No emails added"));
                    break;
                case "de":
                    emailList.setPlaceholder(new Text("Keine E-Mails hinzugefügt"));
                    break;
                case "fr":
                    emailList.setPlaceholder(new Text("Aucun e-mail ajouté"));
                    break;
                case "nl":
                    emailList.setPlaceholder(new Text("Geen e-mails toegevoegd"));
                    break;
            }
            inviteButton.setDisable(true);
        }

        // Add a listener to the text field to clear the warning text when it's empty
        inviteField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                if (newValue.isEmpty()) {
                    warningText.setText("");
                    successText.setText("");
                }
            }
        });


        initializeShortcuts();

        if (event != null)
            initializeEmailList();

        // Bind email list with ListView
        emailList.setItems(emails);
    }

    private void initializeEmailList() {
        inviteTitle.setText("Invite "+ event.getTitle()); // Automatic mails added to the list
        List<Participant> participants = server.
                getParticipantsFromEvent(event.getId());
        for (Participant participant : participants) {
            if (!uniqueEmails.contains(participant.getMail())) {
                uniqueEmails.add(participant.getMail());
                emails.add(participant.getMail());
                inviteButton.setDisable(false);
            }
        }
    }



    private void initializeShortcuts() {
        // Add listener to the text field to handle Enter key press event
        inviteField.setOnKeyPressed(event -> {
            if (event.getCode() ==
                    KeyCode.ENTER) {
                addEmail();
            } else if (event.getCode() == KeyCode.DOWN) {
                inviteAdd.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                cancelButton.requestFocus();
            }
        });
        inviteAdd.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                emailList.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                inviteField.requestFocus();
            }
        });
        cancelButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                inviteField.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                inviteAdd.requestFocus();
            }
        });
        emailList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                inviteAdd.requestFocus();
            } else if (event.getCode() == KeyCode.DOWN) {
                removeButton.requestFocus();
            }
        });
        removeButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                emailList.requestFocus();
            } else if (event.getCode() == KeyCode.DOWN) {
                inviteButton.requestFocus();
            }
        });
        root.addEventFilter(KeyEvent.KEY_PRESSED,event -> {
            if (event.getCode() ==
                    KeyCode.ESCAPE) {
                cancelInvite(
                        new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0,
                                0, null, 0, false, false, false, false,
                                false, false, false, false, false, false, null));
            }
        });
        cancelButton.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                cancelInvite(
                        new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0,
                                0, null, 0, false, false, false, false,
                                false, false, false, false, false, false, null));
            }
        });
    }

    @FXML
    public void addEmail() {
        String newEmail = inviteField.getText().trim();
        String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
        if (!newEmail.isEmpty() && isValidEmail(newEmail) && !uniqueEmails.contains(newEmail)) {
            emails.add(newEmail);
            uniqueEmails.add(newEmail);
            inviteField.clear(); // Clear the text field after adding the email
            inviteButton.setDisable(false);
            warningText.setText("");
        } else if (uniqueEmails.contains(newEmail)) {
            switch (l) {
                case "en":
                    warningText.setText("This email is already added!");
                    break;
                case "de":
                    warningText.setText("Diese E-Mail wurde bereits hinzugefügt!");
                    break;
                case "fr":
                    warningText.setText("Cet e-mail est déjà ajouté!");
                    break;
                case "nl":
                    warningText.setText("Deze e-mail is al toegevoegd!");
                    break;
            }
        } else {
            switch (l) {
                case "en":
                    warningText.setText("Please enter a valid email address!");
                    break;
                case "de":
                    warningText.setText("Bitte geben Sie eine gültige E-Mail-Adresse ein!");
                    break;
                case "fr":
                    warningText.setText("Veuillez saisir une adresse e-mail valide !");
                    break;
                case "nl":
                    warningText.setText("Voer een geldig e-mailadres in!");
                    break;
            }

        }
    }

    boolean isValidEmail(String email) {
        // Regular expression for basic email validation
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @FXML
    public void removeEmail() {
        int selectedIndex = emailList.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String email = emails.get(selectedIndex); // Remove the email from the list
            uniqueEmails.remove(email);
            emails.remove(selectedIndex);
        }
        if (emails.isEmpty()) {
            inviteButton.setDisable(true);
        }
    }

    @FXML
    public void sendInvitation() {
        for (String email : emails) {
            inviteUtils.sendInvitation(email, inviteCode.getText());
        }
        successText.setText("Invitations sent successfully!");
        String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
        switch (l) {
            case "en":
                successText.setText("Invitations sent successfully!");
                break;
            case "de":
                successText.setText("Einladungen erfolgreich gesendet!");
                break;
            case "fr":
                successText.setText("Invitations envoyées avec succès !");
                break;
            case "nl":
                successText.setText("Uitnodigingen succesvol verzonden!");
                break;
        }
    }


    @FXML
    public void generateInviteCode() {
        if (!isGenerated) {
            // Generate invite code
            String code = inviteUtils.generateRandomInviteCode();
            inviteCode.setText(code);
            isGenerated = true;
        }
    }

    static URL getLocation(String path) {
        return client.scenes.InviteCtrl.class.getClassLoader().getResource(path);
    }

    public Collection<String> getEmails() {
        return uniqueEmails;
    }

    public void updateUIWithBundle(ResourceBundle bundle) {
        inviteTitle.setText(bundle.getString("inviteTitle"));
        desc1.setText(bundle.getString("desc1"));
        desc2.setText(bundle.getString("desc2"));
        inviteAdd.setText(bundle.getString("inviteAdd"));
        removeButton.setText(bundle.getString("removeButton"));
        inviteButton.setText(bundle.getString("inviteButton"));
        cancelButton.setText(bundle.getString("cancel"));
        copyButton.setText(bundle.getString("copyButton"));

    }

    @FXML
    public void cancelInvite(MouseEvent mouseEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

    }
    public Text getWarningText() {
        return warningText;
    }

    @FXML
    void copyInviteCode() {
        String code = inviteCode.getText();
        copyTextToClipboard(code);
    }
    @FXML
    int handleInviteCodeMouseSelection(MouseEvent mouseEvent) {
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED
                && mouseEvent.getClickCount() == 2) {
            String code = inviteCode.getText();
            copyTextToClipboard(code);
            successText.setText("Invite code copied to clipboard!");
            return 1;
        } else
            return 0;
    }


    void copyTextToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
        successText.setText("Invite code copied to clipboard!");
    }
}
