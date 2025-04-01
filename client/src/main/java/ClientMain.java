import chess.*;

import server.*;
import ui.Repl;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client ♕");

        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        ServerFacade server = new ServerFacade(serverUrl);
        new Repl(server).run();
    }
}

// TODO:
//      1. Make commands and messages
//      2. Create WebSocketHandler
//      3. Update Server.java
//      4. Update ServerFacade for new WS commands
//      5. Update UI for printing the current board
//      6. Update UI for the help commands
//      7. Update repl loop