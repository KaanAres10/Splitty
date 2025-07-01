package client.utils;

import commons.User;

public class UserServer {
    User user;
    String serverUrl;

    public UserServer() {
    }

    public UserServer(User user, String serverUrl) {
        this.user = user;
        this.serverUrl = serverUrl;
    }
}
