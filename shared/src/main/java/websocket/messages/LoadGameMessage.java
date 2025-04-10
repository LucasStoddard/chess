package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {
    ChessGame game;

    public LoadGameMessage(ChessGame newGame) {
        super(ServerMessageType.LOAD_GAME);
        game = newGame;
    }

    public ChessGame getGame() {
        if (game == null) {
            return new ChessGame();
        } else {
            return game;
        }
    }

}
