package client.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LocalStorage {

    private static final String USER_FILE_PATH = "user.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Stores the user object associated with a server URL.
     *
     * @param userServer The UserServer object containing the user and server URL.
     */
    public static void storeUser(UserServer userServer) {
        Path path = Paths.get(USER_FILE_PATH);
        try {
            Map<String, User> userMap = readAllUsers();
            userMap.put(userServer.serverUrl, userServer.user);
            String userJson = OBJECT_MAPPER.writeValueAsString(userMap);
            Files.write(path, Collections.singleton(userJson), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads all users mapped by server URL.
     *
     * @return A map of server URL to User objects.
     */
    public static Map<String, User> readAllUsers() {
        Path path = Paths.get(USER_FILE_PATH);
        if (Files.exists(path)) {
            try {
                String userJson = Files.readString(path);
                return OBJECT_MAPPER.readValue(userJson, new TypeReference<Map<String, User>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    /**
     * Deletes a user associated with a specific server URL.
     *
     * @param serverUrl The server URL of the user to be deleted.
     */
    public static void deleteUser(String serverUrl) {
        Path path = Paths.get(USER_FILE_PATH);
        try {
            Map<String, User> users = readAllUsers();
            users.remove(serverUrl);
            String updatedJson = OBJECT_MAPPER.writeValueAsString(users);
            Files.write(path, Collections.singleton(updatedJson), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String toJson(JsonWrapperClass jsonWrapperClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(jsonWrapperClass);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonWrapperClass fromJson(String jsonString) {
        JsonWrapperClass jsonWrapperClass = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonWrapperClass = objectMapper.readValue(jsonString, JsonWrapperClass.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonWrapperClass;
    }
}
