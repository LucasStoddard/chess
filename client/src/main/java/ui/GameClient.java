package ui;

import model.UserData;
import server.ServerFacade;
import java.util.Arrays;
import model.ResponseException;

import static ui.EscapeSequences.*;

public class GameClient {
    private ServerFacade serverFacade;

    public GameClient(String serverUrl) {
            serverFacade = new ServerFacade(serverUrl);
    }
}
