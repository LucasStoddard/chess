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
//      1. Make user commands and server messages (DONE)
//      2. Create WebSocketHandler
//          2.1. Create UserGameCommand handlers
//          2.2. Create ServerMessage handlers

// TODO: This is where I currently am, it took ages to figure out WebSocketSessions
//       at the moment, it seems like after adding some more getters to the service
//       classes, I'll be able to build out the web socket commands
//       I still need to expand upon the notifications in general and add one for
//       broadcasting notifications. The current most helpful document is
//       "Rodham-ChessWebSocketDesign.png"

// TODO AFTER:
//      3. Update Server.java
//      4. Update ServerFacade for new WS commands
//      5. Update UI for printing the current board
//      6. Update UI for the help commands
//      7. Update repl loop