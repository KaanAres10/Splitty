package client.scenes;

import client.utils.LanguageManager;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;

import static client.scenes.InviteCtrl.getLocation;
import static org.junit.jupiter.api.Assertions.*;

public class InviteCtrlTest extends ApplicationTest {

    private InviteCtrl inviteCtrl;

    @BeforeAll
    public static void setupSpec() {
        // Optimize TestFX configuration
        System.setProperty("testfx.robot.move_max_count", "1");
        System.setProperty("testfx.robot.write_sleep", "1");
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    @Override
    public void start(Stage stage) throws Exception {
        var fxml = new FXMLLoader();
        fxml.setLocation(getLocation("client/scenes/Invite.fxml"));
        var scene = new Scene(fxml.load());
        stage.setScene(scene);
        stage.show();
        inviteCtrl = fxml.getController(); // Initialize inviteCtrl
        LanguageManager.getInstance().setCurrentLocale(new Locale("en"));
        ResourceBundle bundle = ResourceBundle.getBundle("labels", new Locale("en"));
        inviteCtrl.updateUIWithBundle(bundle);
    }

    @Test
    public void testInitializeShortcuts() {
        // Simulate pressing ENTER on inviteField
        interact(()->push(KeyCode.UP));
        assertTrue(inviteCtrl.cancelButton.isFocused());
        interact(()->push(KeyCode.DOWN));
        assertTrue(inviteCtrl.inviteField.isFocused());
        interact(()->push(KeyCode.UP));
        interact(()->push(KeyCode.UP));
        assertTrue(inviteCtrl.inviteAdd.isFocused());
        interact(()->push(KeyCode.UP));
        interact(() -> push(KeyCode.ENTER));
        assertTrue(inviteCtrl.inviteButton.isDisabled());
        interact(()->push(KeyCode.DOWN));
        assertTrue(inviteCtrl.inviteAdd.isFocused());
        interact(()->push(KeyCode.DOWN));
        assertTrue(inviteCtrl.emailList.isFocused());
        interact(()->push(KeyCode.UP));
        assertTrue(inviteCtrl.inviteAdd.isFocused());
        interact(()->push(KeyCode.UP));
        assertTrue(inviteCtrl.inviteField.isFocused());
        interact(()->push(KeyCode.DOWN));
        interact(()->push(KeyCode.DOWN));
        interact(()->push(KeyCode.DOWN));
        assertTrue(inviteCtrl.removeButton.isFocused());
        interact(()->push(KeyCode.DOWN));
        interact(()->push(KeyCode.UP));
        assertTrue(inviteCtrl.emailList.isFocused());
    }
    @Test
    void testCancelInvite() {
        // Test canceling invitation
        interact(() -> clickOn("#cancelButton"));
        assertTrue(inviteCtrl.cancelButton.isFocused());
    }
    @Test
    public void testAddEmail() {
        // Test adding an email
        String testEmail = "test@example.com";
        interact(() -> {
            clickOn("#inviteField").write(testEmail);
            clickOn("#inviteAdd");
        });
        assertTrue(inviteCtrl.getEmails().contains(testEmail));
        assertTrue(inviteCtrl.emailList.getItems().contains(testEmail));
        assertFalse(inviteCtrl.inviteButton.isDisabled());
        assertTrue(inviteCtrl.inviteField.getText().isEmpty());
        assertTrue(inviteCtrl.getWarningText().getText().isEmpty());
    }

    @Test
    public void testAddEmailThatAddedBefore() {
        String testEmail = "test@example.com";
        interact(() -> {
            clickOn("#inviteField").write(testEmail);
            clickOn("#inviteAdd");
        });
        interact(() -> {
            inviteCtrl.inviteField.requestFocus();
            clickOn("#inviteField").clickOn().write(testEmail);
            clickOn("#inviteAdd");
        });
        assertEquals("This email is already added!",
                inviteCtrl.getWarningText().getText());
    }
    @Test
    public void testAddEmailInvalid() {
        // Test adding an email
        interact(() -> {
            clickOn("#inviteAdd");});
        assertEquals("Please enter a valid email address!",
                inviteCtrl.getWarningText().getText());
    }

    @Test
    public void testRemoveEmail() {
        String testEmail = "test@example.com";
        // Add the email first
        interact(() -> {
            clickOn("#inviteField").write(testEmail);
            clickOn("#inviteAdd");
        });
        // Then remove the email
        interact(() -> {
            ListView<String> emailListView = lookup("#emailList").query();
            assertNotNull(emailListView);
            assertTrue(emailListView.getItems().contains(testEmail));
            emailListView.getSelectionModel().select(testEmail);
            clickOn("#removeButton");
        });
        assertFalse(inviteCtrl.getEmails().contains(testEmail));
    }

    @Test
    public void testSendInvitation() {
        // Test sending invitations
        String testEmail1 = "areskaan107@hotmail.com";
        interact(() -> {
            clickOn("#inviteField").write(testEmail1);
            clickOn("#inviteAdd");
            clickOn("#inviteButton");
        });
        Collection<String> emails = inviteCtrl.getEmails();
        assertTrue(emails.contains(testEmail1));

        Platform.runLater(() -> {
            assertEquals("Invitations sent successfully!",
                    inviteCtrl.getSuccessText().getText());
        });
    }

    @Test
    public void testGenerateInviteCode() {
        // Test generating invite code
        interact(() -> clickOn("#inviteCode"));
        assertNotNull(inviteCtrl.getInviteCode().getText());
    }

    @Test
    void testIsValidEmailValidEmail() {
        // Test valid email addresses
        assertTrue(inviteCtrl.isValidEmail("test@example.com"));
        assertTrue(inviteCtrl.isValidEmail("kaan.altintas@example.co.nl"));
    }

    @Test
    void testIsValidEmailInvalidEmail() {
        // Test invalid email addresses
        assertFalse(inviteCtrl.isValidEmail("example@.com"));
        assertFalse(inviteCtrl.isValidEmail("example.com"));
        assertFalse(inviteCtrl.isValidEmail("example@domain"));
    }

    @Test
    void testAddEmailValidEmail() {
        // Test adding a valid email
        interact(() -> {
            inviteCtrl.inviteField.setText("test@example.com");
            inviteCtrl.addEmail();
        });
        assertTrue(inviteCtrl.getEmails().contains("test@example.com"));
    }


    @Test
    void testAddEmailInvalidEmail() {
        // Test adding an invalid email
        interact(() -> {
            inviteCtrl.inviteField.setText("invalid-email");
            inviteCtrl.addEmail();
        });
        assertFalse(inviteCtrl.getEmails().contains("invalid-email"));
    }

    @Test
    void testAddEmailDuplicateEmail() {
        // Test adding a duplicate email
        interact(() -> {
            inviteCtrl.inviteField.setText("test@example.com");
            inviteCtrl.addEmail();
            inviteCtrl.inviteField.setText("test@example.com");
            inviteCtrl.addEmail();
        });
        assertEquals(1, inviteCtrl.getEmails().size());
    }

    // Simulate copying to clipboard

}
