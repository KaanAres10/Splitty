package client.scenes;

import client.utils.LanguageManager;
import client.utils.LocalStorage;
import client.utils.ServerUtils;
import client.utils.UserServer;
import com.google.inject.Inject;
import commons.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class SettingsCtrl implements Initializable {

    private final Map<String, Image> flagImages = new HashMap<>();
    ResourceBundle bundle;
    @FXML
    Label username;
    @FXML
    TextField usernameBox;
    @FXML
    Label language;
    @FXML
    ComboBox<String> languageBox;
    @FXML
    Label server;
    @FXML
    TextField serverBox;
    @FXML
    Label newLanguage;
    @FXML
    TextField newLanguageTitle;
    @FXML
    TextArea newLanguageText;
    @FXML
    Label settings;
    @FXML
    Button overviewReturn;
    @FXML
    Button adminButton;
    @FXML
    Button connectButton;
    @FXML
    Button sendButton;
    @FXML
    AnchorPane root;
  
    private MainCtrl mainCtrl;
    private ServerUtils serverUtils;
    private String lastConnectedServer;

    @Inject
    public SettingsCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = new ServerUtils();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        languageBox.getItems().addAll("English", "Dutch", "French", "German");

        serverBox.setText(LanguageManager.getInstance().getServer());
        lastConnectedServer = serverBox.getText();
        Map<String, User> users;
        if (LocalStorage.readAllUsers() != null){
            users = LocalStorage.readAllUsers();
        } else {
            users = new HashMap<>(); // needed for testing
        }
        var user = users.get(serverBox.getText());
        if (user != null) {
            usernameBox.setText(user.getUsername());
        }

        languageBox.setCellFactory(new Callback<>() {
            public ListCell<String> call(ListView<String> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(item);
                            // Load flag image based on the language
                            ImageView imageView = new ImageView();
                            imageView.setFitHeight(16); // Adjust height as needed
                            imageView.setFitWidth(16); // Adjust width as needed
                            // Load the image based on the language
                            switch (item) {
                                case "English":
                                    Image image = new Image(getClass().getResourceAsStream("/client/assets/english-flag.png"));
                                    imageView.setImage(image);
                                    break;
                                case "Dutch":
                                    Image image2 = new Image(getClass().getResourceAsStream("/client/assets/dutch-flag.png"));
                                    imageView.setImage(image2);
                                    break;
                                case "French":
                                    Image image3 = new Image(getClass().getResourceAsStream("/client/assets/french-flag.png"));
                                    imageView.setImage(image3);
                                    break;
                                case "German":
                                    Image image4 = new Image(getClass().getResourceAsStream("/client/assets/german-flag.png"));
                                    imageView.setImage(image4);
                                    break;
                            }
                            setGraphic(imageView);
                        }
                    }
                };
            }
        });

        languageBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    // Load flag image based on the language
                    ImageView imageView = new ImageView();
                    imageView.setFitHeight(16); // Adjust height as needed
                    imageView.setFitWidth(16); // Adjust width as needed
                    // Load the image based on the language
                    switch (item) {
                        case "English":
                            imageView.setImage(new Image(getClass().getResourceAsStream("/client/assets/english-flag.png")));
                            break;
                        case "Dutch":
                            imageView.setImage(new Image(getClass().getResourceAsStream("/client/assets/dutch-flag.png")));
                            break;
                        case "French":
                            imageView.setImage(new Image(getClass().getResourceAsStream("/client/assets/french-flag.png")));
                            break;
                        case "German":
                            imageView.setImage(new Image(getClass().getResourceAsStream("/client/assets/german-flag.png")));
                            break;
                    }
                    setGraphic(imageView);
                }
            }
        });

        LanguageManager languageManager = LanguageManager.getInstance();
        ResourceBundle bundle = languageManager.getBundle();
        languageBox.setValue(getLanguageName(languageManager.getCurrentLocale()));
        initializeShortcuts();
    }

    private void initializeShortcuts() {
        root.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    handleConnectButtonAction(new ActionEvent());
                    break;
                case ESCAPE:
                    handleReturnToOverviewButtonAction(new ActionEvent());
                    break;
            }
        });
    }

    @FXML
    private void openAdmin(ActionEvent event) {
        mainCtrl.showAdminLogIn();
    }

    String getLanguageName(Locale locale) {
        switch (locale.getLanguage()) {
            case "nl":
                return "Dutch";
            case "fr":
                return "French";
            case "de":
                return "German";
            default:
                return "English";
        }
    }

    @FXML
    void switchLanguage() {
        String selectedLanguage = languageBox.getValue();
        Locale selectedLocale = null;
        if (selectedLanguage.equals("Dutch")) {
            selectedLocale = new Locale("nl");
        } else if (selectedLanguage.equals("English")) {
            selectedLocale = new Locale("en");
        } else if (selectedLanguage.equals("French")) {
            selectedLocale = new Locale("fr");
        } else if (selectedLanguage.equals("German")) {
            selectedLocale = new Locale("de");
        }
        if (selectedLocale != null) {
            LanguageManager.getInstance().setCurrentLocale(selectedLocale);
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();
            updateUIWithBundle(bundle);
            handleLanguageChange(bundle);
        }
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    @FXML
    public void handleLanguageChange(ResourceBundle bundle) {
        mainCtrl.updateLanguage(bundle);
    }

    public void updateUIWithBundle(ResourceBundle bundle) {
        username.setText(bundle.getString("username"));
        language.setText(bundle.getString("language"));
        server.setText(bundle.getString("server"));
        serverBox.setPromptText(bundle.getString("serverBox"));
        newLanguage.setText(bundle.getString("newLanguage"));
        newLanguageTitle.setPromptText(bundle.getString("newLanguageTitle"));
        newLanguageText.setPromptText(bundle.getString("newLanguageText"));
        settings.setText(bundle.getString("settings"));
        overviewReturn.setText(bundle.getString("overviewReturn"));
        adminButton.setText(bundle.getString("adminButton"));
        connectButton.setText(bundle.getString("connectButton"));
        sendButton.setText(bundle.getString("sendButton"));
    }

    @FXML
    private void handleConnectButtonAction(ActionEvent event) {
        String serverAddress = serverBox.getText();
        String language = String.valueOf(LanguageManager.getInstance().getCurrentLocale().getLanguage());

        if (serverAddress.equals(lastConnectedServer)) {
            switch (language) {
                case "nl":
                    showAlert("Verbinding Mislukt",
                            "U kunt niet opnieuw verbinding maken met de server waarmee u al verbonden bent.",
                            Alert.AlertType.ERROR);
                    break;
                case "fr":
                    showAlert("Échec de la Connexion",
                            "Vous ne pouvez pas vous reconnecter au serveur auquel vous êtes déjà connecté.",
                            Alert.AlertType.ERROR);
                    break;
                case "de":
                    showAlert("Verbindung Fehlgeschlagen",
                            "Sie können sich nicht erneut mit dem Server verbinden, mit dem Sie bereits verbunden sind.",
                            Alert.AlertType.ERROR);
                    break;
                default:
                    showAlert("Connection Failed",
                            "You cannot reconnect to the server you are already connected to.",
                            Alert.AlertType.ERROR);
                    break;
            }
            return;
        }

        if (ServerUtils.testServerConnection(serverAddress)) {
            ServerUtils.setServer(serverAddress);
            lastConnectedServer = serverAddress;
            changeUser(usernameBox.getText(), language);
            switch (language) {
                case "nl":
                    showAlert("Verbinding Geslaagd",
                            "Succesvol verbonden met de server.",
                            Alert.AlertType.INFORMATION);
                    break;
                case "fr":
                    showAlert("Connexion Réussie",
                            "Connecté avec succès au serveur.",
                            Alert.AlertType.INFORMATION);
                    break;
                case "de":
                    showAlert("Verbindung Erfolgreich",
                            "Erfolgreich mit dem Server verbunden.",
                            Alert.AlertType.INFORMATION);
                    break;
                default:
                    showAlert("Connection Successful",
                            "Successfully connected to the server.",
                            Alert.AlertType.INFORMATION);
                    break;
            }
        } else {
            switch (language) {
                case "nl":
                    showAlert("Verbinding Mislukt",
                            "Kan geen verbinding maken met de server. Controleer het adres en probeer het opnieuw.",
                            Alert.AlertType.ERROR);
                    break;
                case "fr":
                    showAlert("Échec de la Connexion",
                            "Impossible de se connecter au serveur. Veuillez vérifier l'adresse et réessayer.",
                            Alert.AlertType.ERROR);
                    break;
                case "de":
                    showAlert("Verbindung Fehlgeschlagen",
                            "Konnte keine Verbindung zum Server herstellen. Bitte überprüfen Sie die Adresse und versuchen Sie es erneut.",
                            Alert.AlertType.ERROR);
                    break;
                default:
                    showAlert("Connection Failed",
                            "Could not connect to the server. Please check the address and try again.",
                            Alert.AlertType.ERROR);
                    break;
            }
        }
    }

    private void changeUser(String username, String preferredLanguage) {
        try {
            // Update to handle user-server association
            var users = LocalStorage.readAllUsers();
            var user = users.get(serverBox.getText());
            if (user == null) {
                user = new User(username, preferredLanguage);
                var userCreated = serverUtils.createUser(user);
                user.setId(userCreated.getId());
                user = serverUtils.updateUser(user);
            } else {
                user.setUsername(username);
                user.setPreferredLanguage(preferredLanguage);
            }
            LocalStorage.storeUser(new UserServer(user, serverBox.getText()));
            mainCtrl.updateUserInStartScreen(user);
        } catch (Exception e) {
            e.printStackTrace();
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to change user");
            alert.showAndWait();
        }
    }


    @FXML
    public void handleReturnToOverviewButtonAction(ActionEvent event) {
        String username = usernameBox.getText();
        Map<String, User> users;
        if (LocalStorage.readAllUsers() != null){
            users = LocalStorage.readAllUsers();
        } else {
            users = new HashMap<>();// needed for testing
        }
        var user = users.get(serverBox.getText());
        if (user != null) {
            user.setUsername(username);
            // Store user with server URL
            LocalStorage.storeUser(new UserServer(user, serverBox.getText()));
            serverUtils.updateUser(user);
        } else {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("User not found");
            alert.showAndWait();
        }
        // Return to the overview
        mainCtrl.showStartScreen();
    }


    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}
