package ui;

import chess.*;
import websocket.messages.*;

public interface GameHandler {
    void updateGame(ChessGame game);
    void printMessage(String message);
}
