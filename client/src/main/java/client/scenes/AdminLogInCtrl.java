package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminLogInCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    Button logInButton;
    @FXML
    Button goBackButton;
    @FXML
    Label incorrectPasswordLabel;
    @FXML
    public PasswordField passwordField;

    @FXML
    private AnchorPane root;

    @FXML
    Label adminLabel;
    @Inject
    public AdminLogInCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void refresh(){
        clearFields();
        incorrectPasswordLabel.setVisible(false);
    }
    @FXML
    public void goBackButtonPressed(ActionEvent actionEvent) {
        clearFields();
        mainCtrl.showSettings();
    }
    @FXML
    public void logInButtonPressed(ActionEvent actionEvent) {
        if(checkPassword()){
            clearFields();
            // need to switch to another scene
            mainCtrl.showAdminCurrentEvents();
        } else {
            clearFields();
            incorrectPasswordLabel.setVisible(true);
        }

    }
    public boolean checkPassword(){
        //here I'll have to add a password check
        String enteredString = passwordField.getText();

        return server.checkPassword(enteredString);
    }
    public void clearFields(){
        passwordField.clear();
    }

    public void updateUIWithBundle(ResourceBundle bundle){
        goBackButton.setText(bundle.getString("goBackButton"));
        adminLabel.setText(bundle.getString("adminLabel"));
        passwordField.setPromptText(bundle.getString("passwordField"));
        incorrectPasswordLabel.setText(bundle.getString("incorrectPasswordLabel"));
        logInButton.setText(bundle.getString("logInButton"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeShortcuts();
    }

    private void initializeShortcuts() {
        root.setOnKeyPressed(e -> {
            if (e.getCode() ==
                    KeyCode.BACK_SPACE ||
                    e.getCode() == KeyCode.ESCAPE) {
                goBackButtonPressed(new ActionEvent());
            }
            if (e.getCode() == KeyCode.ENTER) {
                logInButtonPressed(new ActionEvent());
            }
        });
        goBackButton.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                goBackButtonPressed(new ActionEvent());
            }
        });
        logInButton.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                logInButtonPressed(new ActionEvent());
            }
        });

    }

}
