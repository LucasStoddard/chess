package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {
    ChessGame game;

    public LoadGameMessage(ChessGame newGame) {
        super(ServerMessageType.NOTIFICATION);
        game = newGame;
    }

    public ChessGame getGame() {
        return game;
    }

}
