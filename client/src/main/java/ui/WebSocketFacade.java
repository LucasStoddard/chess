package ui;

import com.google.gson.Gson;
import websocket.commands.*;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

import chess.*;
import model.*;

// NOTE: I think it is simply a coincidence that GameHandler (interface) and GameHandler (class)
//       are named exactly the same, I was a little lost on that.

public class WebSocketFacade extends Endpoint {
    Session session;
    GameHandler gameHandler;

    public WebSocketFacade(String url, GameHandler newGameHandler) throws ResponseException {
        gameHandler = newGameHandler;
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    try {
                        gameHandler.printMessage(message);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                }
            });

        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, Integer gameID, ChessGame.TeamColor teamColor) throws ResponseException {
        try {
            var command = new ConnectCommand(authToken, gameID, teamColor);
            sendMessage(new Gson().toJson(command));
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove chessMove) throws ResponseException {
        try {
            var command = new MakeMoveCommand(authToken, gameID, chessMove);
            sendMessage(new Gson().toJson(command));
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void leaveGame(String authToken, Integer gameID) throws ResponseException {
        try {
            var command = new LeaveCommand(authToken, gameID);
            sendMessage(new Gson().toJson(command));
            this.session.close();
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void resignGame(String authToken, Integer gameID) throws ResponseException {
        try {
            var command = new ResignCommand(authToken, gameID);
            sendMessage(new Gson().toJson(command));
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void sendMessage(String message) throws Exception {
        this.session.getBasicRemote().sendText(message);
    }
}
