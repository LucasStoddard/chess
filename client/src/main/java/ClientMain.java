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
//      2. Create WebSocketHandler (DONE)
//          2.1. Create UserGameCommand handlers (DONE)
//          2.2. Create ServerMessage handlers (DONE)
//      3. Create WebSocketFacade (DONE)
//          3.1. I think it has to extend endpoint? Strange (DONE)
//          3.2. Add functions such that they align with "WebSocket Interactions" (DONE)
//               (ALSO make sure the order is the same as the sequence diagrams)
//               (The Rodham document is extremely helpful here)
//      4. CURRENT STEP: Update gameUI for GameHandler interface

// NOTE: I should look into how to interact with connection manager (I think that is basically what
//       in my code is "WebSocketSessions"

// TODO AFTER:
//      ?. Implement WSserver
//      4. Update Server.java
//      5. Update UI for printing the current board
//      6. Update UI for the help commands
//      7. Update repl loop

// NOTE: I have a lot of error handling in WebSocketHandler, when it should be beforehand in WebSocketFacade
//       This doesn't have to be changed immediately, I just need to transfer the functionality
// NOTE: I don't know where this fits in, but after WebSocketHandler (which handles server to client)
//       I need to implement client to server stuff (CONNECT, LOAD_GAME, etc.) and I think this is done
//                                                  (DIFFERENT FROM MESSAGES THAT GO BY SIMILAR NAMES)
//       through wsFacade, so that should come before updating server.java