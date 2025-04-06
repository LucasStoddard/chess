import com.google.gson.Gson;
import ui.GameHandler;
import websocket.commands.*;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Scanner;
import websocket.commands.*;
import websocket.messages.*;
import chess.*;
import com.google.gson.Gson;
import model.*;

// NOTE: I think it is simply a coincidence that GameHandler (interface) and GameHandler (class)
//       are named exactly the same, I was a little lost on that.

public class WebSocketFacade extends Endpoint {
    Session session;
    GameHandler gameHandler;

    public WebSocketFacade(Session newSession, GameHandler newGameHandler) {
        session = newSession;
        gameHandler = newGameHandler;
    }

    // TODO: Add override for onOpen, onClose, and onError


    // onMessage handler
    public void onMessage(ServerMessage message) throws IOException {
        try {
            switch (message.getServerMessageType()) {
                case LOAD_GAME -> connect(session, username, (ConnectCommand) command);
                case ERROR -> connect(session, username, (ConnectCommand) command);
                case NOTIFICATION -> connect()
            }
        } catch (Exception e) {
            e.printStackTrace();
            serverError(session, new Error("Error: " + e.getMessage()));
        }
    }

    public String connect() {
        try {

        } catch (Exception e) {

        }
    }

    public String makeMove() {
        try {

        } catch (Exception e) {

        }
    }

    public String leaveGame() {
        try {

        } catch (Exception e) {

        }
    }

    public String resignGame() {
        try {

        } catch (Exception e) {

        }
    }

    // THIS IS LIKE THE MESSAGE CONSTRUCTOR
    private void sendMessage() {

    }
}
