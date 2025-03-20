package ui;

import server.ServerFacade;
import java.util.Arrays;
import model.ResponseException;

public class GameClient {
    private String url;
    private ServerFacade serverFacade;

    public GameClient(String serverUrl) {
        url = serverUrl;
        serverFacade = new ServerFacade(serverUrl);
    }
}
