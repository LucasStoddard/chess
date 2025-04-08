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

// TODO CURRENTLY:
//      1. Finish GameClient
//      2. Flesh out the interactions between GameClient and WebSocket
//      3. Update Repl loop
//      4. Start with WebSocketFacadeTests

// NOTE: I have a lot of error handling in WebSocketHandler, when it should be beforehand in WebSocketFacade
//       This doesn't have to be changed immediately, I just need to transfer the functionality
// NOTE: I don't know where this fits in, but after WebSocketHandler (which handles server to client)
//       I need to implement client to server stuff (CONNECT, LOAD_GAME, etc.) and I think this is done
//                                                  (DIFFERENT FROM MESSAGES THAT GO BY SIMILAR NAMES)
//       through wsFacade, so that should come before updating server.java