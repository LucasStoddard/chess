package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{
    private ChessMove move;

    public MakeMoveCommand(String authToken, Integer gameID, ChessMove newMove) {
        super(CommandType.RESIGN, authToken, gameID);
        move = newMove;
    }

    public ChessMove getMove() {
        return move;
    }
}
