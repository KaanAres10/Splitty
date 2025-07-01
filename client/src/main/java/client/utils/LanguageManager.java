package client.utils;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;


// TODO: should be renamed and merged with LocalStorage
public class LanguageManager {
    private static final String CONFIG_FILE_PATH = "config.properties";
    private static final String LANGUAGE_KEY = "language";
    private static final String SERVER_KEY = "server";

    private static LanguageManager instance;
    private Locale currentLocale;
    public String server;

    private ResourceBundle bundle;

    private LanguageManager() {
        loadConfiguration();
        updateBundle();
    }

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    public static void setInstance(LanguageManager instance) {
        LanguageManager.instance = instance;
    }

    public static String getLanguageKey() {
        return LANGUAGE_KEY;
    }

    public void loadConfiguration() {
        if (!new File(CONFIG_FILE_PATH).exists()) {
            saveDefaultConfiguration();
        }

        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(input);
            String language = properties.getProperty(LANGUAGE_KEY);
            server = properties.getProperty(SERVER_KEY);
            if (language != null && !language.isEmpty()) {
                currentLocale = new Locale(language);
            }
        } catch (IOException ex) {
            // Handle exception (e.g., file not found)
        }
        if (currentLocale == null) {
            currentLocale = Locale.getDefault();
        }
    }

    private void saveConfiguration() {
        Properties properties = new Properties();
        properties.setProperty(LANGUAGE_KEY, currentLocale.getLanguage());
        properties.setProperty(SERVER_KEY, server);
        try (OutputStream output = new FileOutputStream(CONFIG_FILE_PATH)) {
            properties.store(output, null);
        } catch (IOException ex) {
            // Handle exception
        }
    }

    private void updateBundle() {
        bundle = ResourceBundle.getBundle("labels", currentLocale);
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public void setCurrentLocale(Locale locale) {
        this.currentLocale = locale;
        saveConfiguration();
        updateBundle();
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
        saveConfiguration();
    }

    public void saveDefaultConfiguration() {
        Properties properties = new Properties();
        properties.setProperty(LANGUAGE_KEY, Locale.getDefault().getLanguage());
        properties.setProperty(SERVER_KEY, "localhost:8080");
        try (OutputStream output = new FileOutputStream(CONFIG_FILE_PATH)) {
            properties.store(output, null);
        } catch (IOException ex) {
            // Handle exception
        }
    }
}
