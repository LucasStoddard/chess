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
    // message handlers
    private void serverError(Session session, Error message) throws IOException {
        System.out.printf("Error: %s\n", new Gson().toJson(message));
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void serverMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }
    // websocket command handlers
    private void connectCommand(Session session, ConnectCommand command) {

    }

    private void leaveCommand(Session session, LeaveCommand command) {

    }

    private void makeMoveCommand(Session session, MakeMoveCommand command) {

    }

    private void resignCommand(Session session, ResignCommand command) {

    }
}
