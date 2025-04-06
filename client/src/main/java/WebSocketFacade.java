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

// TODO: FOLLOW THIS
//       https://byu.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=155aeaa0-e35e-40fe-94bd-b1a10153d812
//       I was most confused on deserializaion and what onMessage receives, but this video seems to
//       clarify it very well.

public class WebSocketFacade extends Endpoint {
    Session session;
    GameHandler gameHandler;

    public WebSocketFacade(Session newSession, GameHandler newGameHandler) {
        session = newSession;
        gameHandler = newGameHandler;
    }

    // TODO: Add override for onOpen, onClose, and onError

    // onMessage handler
    public void onMessage(String msg) throws IOException {
        UserGameCommand command = new Gson().fromJson(msg, UserGameCommand.class);

        try {
            switch (message.getServerMessageType()) {
                case LOAD_GAME -> connect(session);
                case ERROR -> makeMove(session);
                case NOTIFICATION -> connect();
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
