package client.scenes;

import client.utils.LanguageManager;
import client.utils.ServerUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminLogInCtrlTest extends ApplicationTest {

    private static AdminLogInCtrl adminLogInCtrl;
    private static MainCtrl mainCtrlMock;
    private static ServerUtils server;
    private static LanguageManager languageManager;
    private static Stage stage;

    @BeforeAll
    public static void setupSpec() throws Exception {
        System.setProperty("testfx.robot.move_max_count", "1");
        System.setProperty("testfx.robot.write_sleep", "1");
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");

        mainCtrlMock = mock(MainCtrl.class);
        server = mock(ServerUtils.class);
        adminLogInCtrl = new AdminLogInCtrl(server, mainCtrlMock);
        languageManager = mock(LanguageManager.class);
        stage = mock(Stage.class);
    }

    @Test
    void testUpdateUI(){
        adminLogInCtrl.goBackButton = mock(Button.class);
        adminLogInCtrl.adminLabel = mock(Label.class);
        adminLogInCtrl.passwordField = new PasswordField();
        adminLogInCtrl.incorrectPasswordLabel = mock(Label.class);
        adminLogInCtrl.logInButton = mock(Button.class);

        Locale selectedLocale = new Locale("en");
        LanguageManager.getInstance().setCurrentLocale(selectedLocale);
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
        LanguageManager.getInstance().setBundle(bundle);

        adminLogInCtrl.updateUIWithBundle(bundle);
        assertEquals("Back", adminLogInCtrl.goBackButton.getText());

        Locale selectedLocale2 = new Locale("nl");
        LanguageManager.getInstance().setCurrentLocale(selectedLocale2);
        ResourceBundle bundle2 = LanguageManager.getInstance().getBundle();
        adminLogInCtrl.updateUIWithBundle(bundle2);
        assertEquals("Terug", adminLogInCtrl.goBackButton.getText());
    }

    @Test
    void testLogIn(){
        adminLogInCtrl.passwordField = new PasswordField();
        adminLogInCtrl.incorrectPasswordLabel = new Label();
        adminLogInCtrl.logInButtonPressed(null);

        assertTrue(adminLogInCtrl.incorrectPasswordLabel.isVisible());
        when(adminLogInCtrl.checkPassword()).thenAnswer(new Answer<Boolean>() {
            public Boolean answer(InvocationOnMock invocation) {
                return true;
            }
        });
        adminLogInCtrl.logInButtonPressed(null);
        verify(mainCtrlMock).showAdminCurrentEvents();
    }

    @Test
    void testGoBack(){
        adminLogInCtrl.passwordField = new PasswordField();
        adminLogInCtrl.incorrectPasswordLabel = new Label();
        adminLogInCtrl.goBackButtonPressed(null);
        verify(mainCtrlMock).showSettings();
    }

    @Test
    void testRefresh() {
        adminLogInCtrl.passwordField = new PasswordField();
        adminLogInCtrl.incorrectPasswordLabel = new Label();
        adminLogInCtrl.refresh();
        assertFalse(adminLogInCtrl.incorrectPasswordLabel.isVisible());
    }
}