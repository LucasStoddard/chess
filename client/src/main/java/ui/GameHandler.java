package ui;

import model.GameData;
import websocket.messages.*;

public interface GameHandler {
    GameData updateGame(GameData game);
    String printMessage(ServerMessage message);
}
