package server;

import chess.*;
import com.google.gson.Gson;
import model.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.io.IOException;

public class WebSocketSessions {
    Map<Integer, Set<Session>> sessionMap = new HashMap<>();

    void addSessionToGame(Integer gameID, Session session) {
        if (sessionMap.containsKey(gameID)) {
            Set<Session> newSet = sessionMap.get(gameID);
            newSet.add(session);
            sessionMap.put(gameID, newSet);
        } else {
            Set<Session> newSet = new HashSet<>();
            newSet.add(session);
            sessionMap.put(gameID, newSet);
        }
    }

    void removeSessionFromGame(Integer gameID, Session session) {
        Set<Session> newSet = sessionMap.get(gameID);
        newSet.remove(session);
        sessionMap.put(gameID, newSet);
    }

    int getSessionID(Session session) throws Exception {
        for (int gameIDs : sessionMap.keySet()) {
            if (getSessionsForGame(gameIDs).contains(session)) {
                return gameIDs;
            }
        }
        throw new Exception("Player not found in any session");
    }

    Set<Session> getSessionsForGame(Integer gameID) {
        return sessionMap.get(gameID);
    }
}
