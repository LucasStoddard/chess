package server;

import chess.*;
import com.google.gson.Gson;
import model.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.Session;
import service.GameService;
import service.UserService;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.Set;

@WebSocket
public class WebSocketHandler {
    private final WebSocketSessions wsSessions = new WebSocketSessions();
    UserService userService;
    GameService gameService;

    public WebSocketHandler(UserService userservice, GameService gameservice) {
        userService = userservice;
        gameService = gameservice;
    }

    // NOTE: The onOpen, onClose, and onError are going to be within WSFacade
    // onMessage handler
    public void onMessage(Session session, String msg) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(msg, UserGameCommand.class);

            // Throws a custom UnauthorizedException. Yours may work differently)
            String username = userService.getUsername(command.getAuthToken());

            wsSessions.addSessionToGame(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connectCommand(session, username, (ConnectCommand) command);
                case MAKE_MOVE -> makeMoveCommand(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveCommand(session, username, (LeaveCommand) command);
                case RESIGN -> resignCommand(session, username, (ResignCommand) command);
            }
        } catch (Exception e) {
            e.printStackTrace();
            serverError(session, new Error("Error: " + e.getMessage()));
        }
    }

    // message handlers
    private void serverError(Session session, Error message) throws IOException {
        System.out.printf("Error: %s\n", new Gson().toJson(message));
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void serverMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }

    // false for not notifying the rootUser, true for notifying the user
    private void broadcastMessage(Integer gameID, ServerMessage message, Session session, boolean notifyRootUser) throws IOException {
        for (Session sessions : wsSessions.getSessionsForGame(gameID)) {
            if (sessions != session || notifyRootUser) {
                serverMessage(sessions, message);
            }
        }
    }

    // websocket command handlers
    private void connectCommand(Session session, String username, ConnectCommand command) throws IOException {
        try {
            GameData gameData = gameService.getGame(command.getGameID());
            ChessGame game = gameData.game();
            String teamColorJoin = command.getColor();

            if (teamColorJoin.contains("observer")) {   // observers
                broadcastMessage(gameData.gameID(),
                        new NotificationMessage("%s has joined to observe the game\n".formatted(username)), session, false);
                serverMessage(session, new LoadGameMessage(game));
            } else {                                    // players
                if (gameData.whiteUsername() == null && teamColorJoin.equals("white")) {
                    broadcastMessage(gameData.gameID(),
                            new NotificationMessage("%s has joined as white\n".formatted(username)), session, false);
                    serverMessage(session, new LoadGameMessage(game));
                } else if (gameData.blackUsername() == null && teamColorJoin.equals("black")) {
                    broadcastMessage(gameData.gameID(),
                            new NotificationMessage("%s has joined as black\n".formatted(username)), session, false);
                    serverMessage(session, new LoadGameMessage(game));
                } else {
                    serverError(session, new Error("Error: Bad Color"));
                }
            }
        } catch (Exception e) {
            serverError(session, new Error(e.getMessage()));
        }
    }


    private void leaveCommand(Session session, String username, LeaveCommand command) throws IOException {
        try {

        } catch (Exception e) {
            serverError(session, new Error(e.getMessage()));
        }
    }

    private void makeMoveCommand(Session session, String username, MakeMoveCommand command) {
        try {

        } catch (Exception e) {

        }
    }

    private void resignCommand(Session session, String username, ResignCommand command) {
        try {

        } catch (Exception e) {

        }
    }
}
