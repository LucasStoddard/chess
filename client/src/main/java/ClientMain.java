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

// TODO: I don't know where this fits in, but after WebSocketHandler (which handles server to client)
//       I need to implement client to server stuff (CONNECT, LOAD_GAME, etc.) and I think this is done
//                                                  (DIFFERENT FROM MESSAGES THAT GO BY SIMILAR NAMES)
//       through wsFacade, so that should come before updating server.java

// TODO AFTER:
//      3. Update Server.java
//      4. Update ServerFacade for new WS commands (ACTUALLY MAKE WSserverFACADE)
//      5. Update UI for printing the current board
//      6. Update UI for the help commands
//      7. Update repl loop