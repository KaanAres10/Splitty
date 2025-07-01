package client.scenes;

import client.utils.LanguageManager;
import client.utils.LocalStorage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class SettingsCtrlTest extends ApplicationTest {

    private static SettingsCtrl settingsCtrl;
    private static MainCtrl mainCtrlMock;
    private static LocalStorage localStorage;

    @BeforeAll
    public static void setupSpec() {
        System.setProperty("testfx.robot.move_max_count", "1");
        System.setProperty("testfx.robot.write_sleep", "1");
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");

        mainCtrlMock = mock(MainCtrl.class);
        settingsCtrl = new SettingsCtrl(mainCtrlMock);
        localStorage = mock(LocalStorage.class);
    }

    @Test
    void testInit(){
        settingsCtrl.languageBox = new ComboBox<>();
        settingsCtrl.languageBox.getItems().addAll("English", "Dutch", "French", "German");
        settingsCtrl.serverBox = new TextField();
        settingsCtrl.usernameBox = new TextField();
        settingsCtrl.root = new AnchorPane();

        LanguageManager.getInstance().setCurrentLocale(new Locale("en"));
        settingsCtrl.initialize(null, null);
        assertEquals(settingsCtrl.getLanguageName(LanguageManager.getInstance().getCurrentLocale()),settingsCtrl.languageBox.getValue());
    }


    @Test
    void testSwitchLanguage() {
        settingsCtrl.username = mock(Label.class);
        settingsCtrl.language = mock(Label.class);
        settingsCtrl.server = mock(Label.class);
        settingsCtrl.serverBox = new TextField();
        settingsCtrl.newLanguage = mock(Label.class);
        settingsCtrl.newLanguageTitle = new TextField();
        settingsCtrl.newLanguageText = new TextArea();
        settingsCtrl.settings = mock(Label.class);
        settingsCtrl.overviewReturn = mock(Button.class);
        settingsCtrl.adminButton = mock(Button.class);
        settingsCtrl.connectButton = mock(Button.class);
        settingsCtrl.sendButton = mock(Button.class);
        settingsCtrl.languageBox = new ComboBox<>();

        settingsCtrl.languageBox.setValue("Dutch");
        settingsCtrl.switchLanguage();
        assertEquals(new Locale("nl"), LanguageManager.getInstance().getCurrentLocale());

        settingsCtrl.languageBox.setValue("English");
        settingsCtrl.switchLanguage();
        assertEquals(new Locale("en"), LanguageManager.getInstance().getCurrentLocale());

        settingsCtrl.languageBox.setValue("French");
        settingsCtrl.switchLanguage();
        assertEquals(new Locale("fr"), LanguageManager.getInstance().getCurrentLocale());

        settingsCtrl.languageBox.setValue("German");
        settingsCtrl.switchLanguage();
        assertEquals(new Locale("de"), LanguageManager.getInstance().getCurrentLocale());
    }

    @Test
    public void testGetBundle() {
        ResourceBundle expectedBundle = ResourceBundle.getBundle("labels", Locale.ENGLISH);
        settingsCtrl.bundle = expectedBundle;
        ResourceBundle actualBundle = settingsCtrl.getBundle();

        assertEquals(expectedBundle, actualBundle);
    }

    @Test
    void testUpdateUIWitchBundle(){
        settingsCtrl.username = mock(Label.class);
        settingsCtrl.language = mock(Label.class);
        settingsCtrl.server = mock(Label.class);
        settingsCtrl.serverBox = new TextField();
        settingsCtrl.newLanguage = mock(Label.class);
        settingsCtrl.newLanguageTitle = new TextField();
        settingsCtrl.newLanguageText = new TextArea();
        settingsCtrl.settings = mock(Label.class);
        settingsCtrl.overviewReturn = mock(Button.class);
        settingsCtrl.adminButton = mock(Button.class);
        settingsCtrl.connectButton = mock(Button.class);
        settingsCtrl.sendButton = mock(Button.class);

        Locale selectedLocale = new Locale("en");
        LanguageManager.getInstance().setCurrentLocale(selectedLocale);
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
        LanguageManager.getInstance().setBundle(bundle);

        settingsCtrl.updateUIWithBundle(bundle);
        assertEquals("Username", settingsCtrl.username.getText());

        Locale selectedLocale2 = new Locale("nl");
        LanguageManager.getInstance().setCurrentLocale(selectedLocale2);
        ResourceBundle bundle2 = LanguageManager.getInstance().getBundle();
        settingsCtrl.updateUIWithBundle(bundle2);
        assertEquals("Gebruikersnaam", settingsCtrl.username.getText());
    }

    @Test
    void testOpenAdmin() throws Exception {
        Method method = SettingsCtrl.class.getDeclaredMethod("openAdmin", ActionEvent.class);
        method.setAccessible(true);
        method.invoke(settingsCtrl, (ActionEvent) null);

        verify(mainCtrlMock).showAdminLogIn();
    }
    @Test
    void testHandleReturnToOverviewButtonAction() throws Exception {
        Method method = SettingsCtrl.class.getDeclaredMethod("handleReturnToOverviewButtonAction", ActionEvent.class);
        method.setAccessible(true);
        settingsCtrl.usernameBox = new TextField();

        Platform.runLater(() -> {
            try {
                method.invoke(settingsCtrl, (ActionEvent) null);
                verify(mainCtrlMock).showStartScreen();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testHandleLanguageChange() throws Exception {
        Method method = SettingsCtrl.class.getDeclaredMethod("handleLanguageChange", ResourceBundle.class);
        method.setAccessible(true);
        method.invoke(settingsCtrl, (ActionEvent) null);

        verify(mainCtrlMock).updateLanguage(null);
    }

    @Test
    void testGetLanguageNameDutch() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Locale locale = new Locale("nl");

        Method method = settingsCtrl.getClass().getDeclaredMethod("getLanguageName", Locale.class);
        method.setAccessible(true); // This makes the private method accessible
        assertEquals("Dutch", method.invoke(settingsCtrl, locale));
    }

    @Test
    void testGetLanguageNameFrench() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Locale locale = new Locale("fr");

        Method method = settingsCtrl.getClass().getDeclaredMethod("getLanguageName", Locale.class);
        method.setAccessible(true);
        assertEquals("French", method.invoke(settingsCtrl, locale));
    }

    @Test
    void testGetLanguageNameGerman() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Locale locale = new Locale("de");

        Method method = settingsCtrl.getClass().getDeclaredMethod("getLanguageName", Locale.class);
        method.setAccessible(true);
        assertEquals("German", method.invoke(settingsCtrl, locale));
    }

    @Test
    void testGetLanguageNameEnglish() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Locale locale = new Locale("en");

        Method method = settingsCtrl.getClass().getDeclaredMethod("getLanguageName", Locale.class);
        method.setAccessible(true);
        assertEquals("English", method.invoke(settingsCtrl, locale));
    }

}