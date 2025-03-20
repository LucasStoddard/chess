package ui;

import server.ServerFacade;

public class GameClient {
    private String url;
    private ServerFacade serverFacade;

    public GameClient(String serverUrl) {
        url = serverUrl;
        serverFacade = new ServerFacade(serverUrl);
    }
}
