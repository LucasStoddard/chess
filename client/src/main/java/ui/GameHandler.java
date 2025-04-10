package ui;

import model.GameData;
import model.ResponseException;
import websocket.messages.*;

public interface GameHandler {
    GameData updateGame(GameData game);
    void printMessage(String message) throws ResponseException;
}
