package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.*;
import org.eclipse.jetty.server.Authentication;
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

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(msg, UserGameCommand.class);

            String username = userService.getUsername(command.getAuthToken());

            wsSessions.addSessionToGame(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connectCommand(session, username, new Gson().fromJson(msg, ConnectCommand.class));
                case MAKE_MOVE -> makeMoveCommand(session, username, new Gson().fromJson(msg, MakeMoveCommand.class));
                case LEAVE -> leaveCommand(session, username, new Gson().fromJson(msg, LeaveCommand.class));
                case RESIGN -> resignCommand(session, username, new Gson().fromJson(msg, ResignCommand.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // System.out.println(msg);
            serverMessage(session, new ErrorMessage(e.getMessage()));
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable t) {
        try {
            serverMessage(session, new ErrorMessage(t.getMessage()));
        } catch (Exception e) {
            System.out.printf("onError error: " + e.getMessage());
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int status, String cause) {
        try {
            int gameID = wsSessions.getSessionID(session);
            wsSessions.removeSessionFromGame(gameID, session);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void serverError(Session session, String message) throws IOException {
        session.getRemote().sendString(message);
    }

    private void serverMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }

    // false for not notifying the rootUser, true for notifying the user
    private void broadcastMessage(Integer gameID, ServerMessage message, Session session, boolean notifyRootUser) throws IOException {
        for (Session eachSession : wsSessions.getSessionsForGame(gameID)) {
            if (eachSession != session || notifyRootUser) {
                serverMessage(eachSession, message);
            }
        }
    }

    // websocket command handlers
    public void connectCommand(Session session, String username, ConnectCommand command) throws IOException {
        try {
            GameData gameData = gameService.getGame(command.getGameID());
            ChessGame game = gameData.game();
            ChessGame.TeamColor teamColorJoin = command.getColor();
            if (teamColorJoin == null) {   // observers
                wsSessions.addSessionToGame(gameData.gameID(), session);
                serverMessage(session, new LoadGameMessage(game));
                broadcastMessage(gameData.gameID(),
                        new NotificationMessage("%s has joined to observe the game".formatted(username)), session, false);
            } else { // short circuit                   // players
                if (gameData.whiteUsername() != null && gameData.whiteUsername().contains(username) && teamColorJoin == ChessGame.TeamColor.WHITE) {
                    wsSessions.addSessionToGame(gameData.gameID(), session);
                    serverMessage(session, new LoadGameMessage(game));
                    broadcastMessage(gameData.gameID(),
                            new NotificationMessage("%s has joined as white".formatted(username)), session, false);
                } else if (gameData.blackUsername() != null && gameData.blackUsername().contains(username) && teamColorJoin == ChessGame.TeamColor.BLACK) {
                    wsSessions.addSessionToGame(gameData.gameID(), session);
                    serverMessage(session, new LoadGameMessage(game));
                    broadcastMessage(gameData.gameID(),
                            new NotificationMessage("%s has joined as black".formatted(username)), session, false);
                } else {
                    serverMessage(session, new ErrorMessage("Error: Team already taken"));
                }
            }
        } catch (Exception e) {
            serverMessage(session, new ErrorMessage(e.getMessage()));
        }
    }


    public void leaveCommand(Session session, String username, LeaveCommand command) throws IOException {
        try {
            broadcastMessage(command.getGameID(),
                    new NotificationMessage("%s has left the game\n".formatted(username)), session, false);
            wsSessions.removeSessionFromGame(command.getGameID(), session);
            updateGameService(command.getGameID(), username);
        } catch (Exception e) {
            serverMessage(session, new ErrorMessage(e.getMessage()));
        }
    }

    private void canMove(ChessGame game, GameData gameData) throws Exception {
        if (game.isInCheckmate(ChessGame.TeamColor.WHITE) || game.isInCheckmate(ChessGame.TeamColor.BLACK)
                || game.isInStalemate(game.getTeamTurn())) {
            throw new Exception("Error: The game has already ended, no more moves can be made");
        }
        if (gameData.blackUsername() == null || gameData.whiteUsername() == null) {
            throw new Exception("Error: You must have an opponent in order to move");
        }
    }

    private void noOpponentMove(ChessMove proposedMove, String userName, GameData gameData) throws Exception {
        ChessGame.TeamColor pieceColor = gameData.game().getBoard().getPiece(proposedMove.getStartPosition()).getTeamColor();
        ChessGame.TeamColor userTeam;
        if (userName.contains(gameData.blackUsername())) {
            userTeam = ChessGame.TeamColor.BLACK;
        } else if (userName.contains(gameData.whiteUsername())) {
            userTeam = ChessGame.TeamColor.WHITE;
        } else {
            throw new Exception("Error: Silly observer, you can't move the players!");
        }
        if (userTeam != pieceColor) {
            throw new Exception("Error: Silly player, you can't move for your opponent!");
        }
    }

    public void makeMoveCommand(Session session, String username, MakeMoveCommand command) throws IOException {
        // CHECK MOVE AND MAKE IT
        try {
            resignFilter(session, command.getGameID(), username);
            GameData gameData = gameService.getGame(command.getGameID());
            ChessGame game = gameData.game();
            canMove(game, gameData);
            noOpponentMove(command.getMove(), username, gameData);
            game.makeMove(command.getMove()); // Error checking for move, doesn't actually update game
            gameService.updateGame(new GameData(command.getGameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(), game)); // Updates game using gameService
        } catch (Exception e) {
            serverMessage(session, new ErrorMessage(e.getMessage()));
            return;
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
            serverMessage(session, new ErrorMessage(e.getMessage()));
        }
    }

    public void resignFilter(Session session, int gameID, String username) throws Exception {
        if (!wsSessions.getSessionsForGame(gameID).contains(session)) {
            throw new Exception("Error: You have already resigned");
        }
        GameData game = gameService.getGame(gameID);
        if (game.whiteUsername() == null || game.blackUsername() == null) {
            throw new Exception("Error: Someone need an opponent who hasn't resigned in order to play");
        }
        if (!game.whiteUsername().contains(username) && !game.blackUsername().contains(username)) {
            throw new Exception("Error: You cannot resign as an observer");
        }
    }

    public void resignCommand(Session session, String username, ResignCommand command) throws IOException {
        try {
            resignFilter(session, command.getGameID(), username);
            broadcastMessage(command.getGameID(),
                    new NotificationMessage("%s has resigned or left the game".formatted(username)),
                    session, true);
            wsSessions.removeSessionFromGame(command.getGameID(), session);
            updateGameService(command.getGameID(), username);
        } catch (Exception e) {
            serverMessage(session, new ErrorMessage(e.getMessage()));
        }
    }

    public void updateGameService(int gameID, String username) throws Exception {
        GameData gameData = gameService.getGame(gameID);
        if (gameData.blackUsername().contains(username)) {
            gameService.updateGame(new GameData(gameID, gameData.whiteUsername(),
                    null, gameData.gameName(), gameData.game()));
        } else if (gameData.whiteUsername().contains(username)) {
            gameService.updateGame(new GameData(gameID, null,
                    gameData.blackUsername(), gameData.gameName(), gameData.game()));
        }
    }
}
