package server;

import chess.*;
import com.google.gson.Gson;
import model.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {

    }



    // onMessage handler
    // TODO: look through WebSocket-Spark
    public void onMessage(Session session, String msg) {
        try {
            UserGameCommand command = new Gson().fromJson(msg, UserGameCommand.class);

            // Throws a custom UnauthorizedException. Yours may work differently.
            String username = getUsername(command.getAuthToken());

            saveSession(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connectCommand(session, username, (ConnectCommand) command);
                case MAKE_MOVE -> makeMoveCommand(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveCommand(session, username, (LeaveCommand) command);
                case RESIGN -> resignCommand(session, username, (ResignCommand) command);
            }
        } catch (Exception e) {
            e.printStackTrace();
            serverMessage(session.getSession(), new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    // message handlers
    private void serverError(Session session, String username, Error message) throws IOException {
        System.out.printf("Error: %s\n", new Gson().toJson(message));
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void serverMessage(Session session, String username, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }
    // websocket command handlers
    private void connectCommand(Session session, String username, ConnectCommand command) {
        try {

        } catch (Exception e) {

        }
    }

    private void leaveCommand(Session session, String username, LeaveCommand command) {
        try {

        } catch (Exception e) {

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
