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
        new Repl(server, serverUrl).run();
    }
}

// TODO: Current problem is multiple sessions

// Okay I think I've figured it out, people to server is a one way street, thus the only way we've used serverFacade is to communicate with the
// server, (Ex. logging in, logging out, registering). ui.WebSocketFacade is NOT a one way street. WSfacade is a TWO way street, thus the ever so
// sneaky printMessage will ALSO, before printing the message, use ui.WebSocketFacade to SEND a message to the Server. To visualize here is server
// and client interactions.
// client message -> repl calls -> MainClient which calls -> serverFacade serializes and sends message -> Server (error or doesn't error) THEN
//      serverFacade basically (y/n error?) -> MainClient which returns a string to the repl -> repl prints message -> client can see message
// BUT for WSFacade the interactions can be as follows (KEEP IN MIND: gameClient ONLY USES ui.WebSocketFacade)
// client message -> repl calls -> GameClient which calls -> webSocketFacade which serializes(?) and sends message -> Websocket
// NOW COMPLETELY SEPARATELY
// Websocket sends message -> WebSocketHandler deserializes that message and sends it MULTIPLE or ONE client
// AGAIN SEPARATELY
// WebSocketHandler sends message(s) to client(s) -> ui.WebSocketFacade gets a message -> ui.WebSocketFacade calls GameClient to process message ->
//      GameClient (based upon the message) either sends a board OR a simple message -> REPL prints message -> client can see message

// This is all built to work in any order, so you may be about to make a move, but if someone starts observing, both players need to know, even
// before you've made a move.
// Everything before is action -> server reaction -> response based upon reaction to user
// NOW it is action -> websocket ??? full stop ... websocket action -> websocket reaction -> send message to user

// NOW:
//      0. Finish promotion code and other code for Chess Moves
//      1. Get gameClient completely working
//      2. Get the Server -> Client messages working (and printing) through gameClient

// FUTURE:
//      0. Update Clients to use wsfacade, Get Repl Loop working (?)
//      1. Finish GameClient
//      2. Flesh out the interactions between GameClient and WebSocket
//      3. Update Repl loop
//      4. Start with WebSocketFacadeTests

// NOTE: I have a lot of error handling in WebSocketHandler, when it should be beforehand in ui.WebSocketFacade
//       This doesn't have to be changed immediately, I just need to transfer the functionality
// NOTE: I don't know where this fits in, but after WebSocketHandler (which handles server to client)
//       I need to implement client to server stuff (CONNECT, LOAD_GAME, etc.) and I think this is done
//                                                  (DIFFERENT FROM MESSAGES THAT GO BY SIMILAR NAMES)
//       through wsFacade, so that should come before updating server.java