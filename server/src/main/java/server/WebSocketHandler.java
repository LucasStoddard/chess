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
    // NOTE: This class may need setters for wsSessions so that WSFacade can interact with them

    public WebSocketHandler(UserService userservice, GameService gameservice) {
        userService = userservice;
        gameService = gameservice;
    }

    // onMessage handler
    public void onMessage(Session session, String msg) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(msg, UserGameCommand.class);

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
        System.out.printf("Error: %s", new Gson().toJson(message));
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
                serverMessage(session, new LoadGameMessage(game));
                broadcastMessage(gameData.gameID(),
                        new NotificationMessage("%s has joined to observe the game".formatted(username)), session, false);
                wsSessions.addSessionToGame(gameData.gameID(), session);
            } else {                                    // players
                if (gameData.whiteUsername() == null && teamColorJoin.equals("white")) {
                    serverMessage(session, new LoadGameMessage(game));
                    broadcastMessage(gameData.gameID(),
                            new NotificationMessage("%s has joined as white".formatted(username)), session, false);
                    wsSessions.addSessionToGame(gameData.gameID(), session);
                } else if (gameData.blackUsername() == null && teamColorJoin.equals("black")) {
                    serverMessage(session, new LoadGameMessage(game));
                    broadcastMessage(gameData.gameID(),
                            new NotificationMessage("%s has joined as black".formatted(username)), session, false);
                    wsSessions.addSessionToGame(gameData.gameID(), session);
                } else {
                    serverError(session, new Error("Error: Bad Color"));
                }
            }
        } catch (Exception e) {
            serverError(session, new Error("Error: " + e.getMessage()));
        }
    }


    private void leaveCommand(Session session, String username, LeaveCommand command) throws IOException {
        try {
            broadcastMessage(command.getGameID(),
                    new NotificationMessage("%s has left the game\n".formatted(username)), session, false);
            wsSessions.removeSessionFromGame(command.getGameID(), session);
            session.close();
        } catch (Exception e) {
            serverError(session, new Error(e.getMessage()));
        }
    }
    private void canMove(ChessGame game) throws Exception {
        if (game.isInCheckmate(ChessGame.TeamColor.WHITE) || game.isInCheckmate(ChessGame.TeamColor.BLACK)
                || game.isInStalemate(game.getTeamTurn())) {
            throw new Exception("The game has already ended, no more moves can be made buddy");
        }
    }

    private void makeMoveCommand(Session session, String username, MakeMoveCommand command) throws IOException {
        // CHECK MOVE AND MAKE IT
        try {
            GameData gameData = gameService.getGame(command.getGameID());
            ChessGame game = gameData.game();
            canMove(game); // This is for silly people who try to move if the game is already over
            game.makeMove(command.getMove()); // Error checking for move, doesn't actually update game
            gameService.updateGame(new GameData(command.getGameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(), game)); // Updates game using gameService
        } catch (Exception e) {
            serverError(session, new Error("Error: " + e.getMessage()));
        }
        // AFTER MOVE CODE
        try {
            GameData gameData = gameService.getGame(command.getGameID());
            ChessGame game = gameData.game(); // Move should have already been made
            broadcastMessage(command.getGameID(), new LoadGameMessage(game), session, true);
            broadcastMessage(command.getGameID(),
                    new NotificationMessage("%s has made move %s".formatted(username, command.getMove().toString())),
                    session, false);
            // Lab specifications seems to suggest to broadcast 3 times in the case of "check, checkmate or stalemate"
            if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                broadcastMessage(command.getGameID(),
                        new NotificationMessage("%s is in checkmate!".formatted(gameData.whiteUsername())),
                        session, true);
            } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                broadcastMessage(command.getGameID(),
                        new NotificationMessage("%s is in checkmate!".formatted(gameData.blackUsername())),
                        session, true);
            } else if (game.isInStalemate(game.getTeamTurn())) {
                // A little wierd, but stalemate only matters for the team about to move
                broadcastMessage(command.getGameID(),
                        new NotificationMessage("There is a stalemate!"),
                        session, true);
            } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
                broadcastMessage(command.getGameID(),
                        new NotificationMessage("%s on white is in check...".formatted(gameData.whiteUsername())),
                        session, true);
            } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
                broadcastMessage(command.getGameID(),
                        new NotificationMessage("%s on black is in check...".formatted(gameData.blackUsername())),
                        session, true);
            }
        } catch (Exception e) {
            serverError(session, new Error("Error: " + e.getMessage()));
        }
    }

    private void resignCommand(Session session, String username, ResignCommand command) throws IOException {
        try {
            wsSessions.removeSessionFromGame(command.getGameID(), session);
            broadcastMessage(command.getGameID(),
                    new NotificationMessage("%s has resigned".formatted(username)),
                    session, true);
        } catch (Exception e) {
            serverError(session, new Error("Error: " + e.getMessage()));
        }
    }
}
