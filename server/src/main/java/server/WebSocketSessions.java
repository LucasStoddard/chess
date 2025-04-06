package server;

import chess.*;
import com.google.gson.Gson;
import model.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.*;
import java.util.Map;
import java.util.Set;

import java.io.IOException;

public class WebSocketSessions {
    Map<Integer, Set<Session>> sessionMap;

    void addSessionToGame(Integer gameID, Session session) {
        (sessionMap.get(gameID)).add(session);
    }

    void removeSessionFromGame(Integer gameID, Session session) {
        (sessionMap.get(gameID)).remove(session);
    }

    Set<Session> getSessionsForGame(Integer gameID) {
        return sessionMap.get(gameID);
    }
}
