package websocket.commands;

import chess.*;

public class ConnectCommand extends UserGameCommand {
    ChessGame.TeamColor color;

    public ConnectCommand(String authToken, Integer gameID, ChessGame.TeamColor newColor) {
        super(CommandType.CONNECT, authToken, gameID);
        color = newColor;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }
}
