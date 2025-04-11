package ui;

import chess.*;
import static ui.EscapeSequences.*;

import com.google.gson.Gson;
import model.*;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.Objects;

public class GameUI implements GameHandler {
    WebSocketFacade wsFacade;
    ChessBoard board;
    ChessGame game;
    Boolean isWhite; // For reversing boards
    String authToken;
    int gameID;

    public GameUI() {
        board = new ChessBoard();
        board.resetBoard();
    }

    public void updateGameUI(String auth, int gameId) {
        authToken = auth;
        gameID = gameId;
    }

    public GameData updateGame(GameData newGame) {
        game = newGame.game();
        board = game.getBoard();
        gameID = newGame.gameID();
        return newGame;
    }

    public void updateWebSocketFacade(WebSocketFacade webSocketFacade) {
        wsFacade = webSocketFacade;
    }

    public void printMessage(String message) throws ResponseException {
        if (message.contains("LOAD_GAME")) {
            loadGame(new Gson().fromJson(message, LoadGameMessage.class), isWhite);
        } else if (message.contains("ERROR")) {
            errorClient(new Gson().fromJson(message, ErrorMessage.class));
        } else {
            notifyClient(new Gson().fromJson(message, NotificationMessage.class));
        }
    }

    private void loadGame(LoadGameMessage message, Boolean isReversed) throws ResponseException {
        board = message.getGame().getBoard();
        game = message.getGame();
        System.out.println(getGameString());
    }

    private void errorClient(ErrorMessage message) {
        System.out.println(message.getMessage());
    }

    private void notifyClient(NotificationMessage message) {
        System.out.println(message.getMessage());
    }

    public void updateTeam(Boolean isWhitePlayer) {
        isWhite = isWhitePlayer;
    }


    private String printBoard(boolean isReversed, boolean highlightMoves, ChessPosition position) {
        StringBuilder boardString = new StringBuilder();
        if (isReversed) {
            boardString.append(firstRow(true));
            for (int i = 8; i > 0; i--) {
                boardString.append(otherRow(i, true, highlightMoves, position));
            }
            boardString.append(firstRow(true));
        } else {
            boardString.append(firstRow(false));
            for (int i = 1; i < 9; i++) {
                boardString.append(otherRow(i, false, highlightMoves, position));
            }
            boardString.append(firstRow(false));
        }
        return boardString.toString();
    }

    private String firstRow(boolean isReversed) {
        StringBuilder boardString = new StringBuilder();
        boardString.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_MAGENTA);
        if (isReversed) {
            boardString.append("    a  b  c  d  e  f  g  h    ");
        } else {
            boardString.append("    h  g  f  e  d  c  b  a    ");
        }
        boardString.append(RESET_BG_COLOR + RESET_TEXT_COLOR);
        boardString.append("\n");
        return boardString.toString();
    }

    private String otherRow(int row, boolean isReversed, boolean highlightMoves, ChessPosition position) {
        StringBuilder boardString = new StringBuilder();
        boardString.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_CYAN);
        boardString.append(" %d ".formatted(row));
        Collection<ChessMove> validMoves;
        if (highlightMoves) {
            validMoves = game.validMoves(position);
        } else {
            validMoves = null;
        }
        if (isReversed) {
            for (int i = 1; i < 9; i++) {
                boardString.append(squareColor(row, i, position, validMoves, highlightMoves));
                boardString.append(piece(row, i));
            }
        } else {
            for (int i = 8; i > 0; i--) {
                boardString.append(squareColor(row, i, position, validMoves, highlightMoves));
                boardString.append(piece(row, i));
            }
        }
        boardString.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_CYAN);
        boardString.append(" %d ".formatted(row));
        boardString.append(RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
        return boardString.toString();
    }

    private String piece(int row, int col) {
        StringBuilder boardString = new StringBuilder();
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(position);
        if (piece != null) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                boardString.append(SET_TEXT_COLOR_WHITE);
            } else {
                boardString.append(SET_TEXT_COLOR_BLACK);
            }
            switch (piece.getPieceType()) {
                case ChessPiece.PieceType.PAWN -> boardString.append(" P ");
                case ChessPiece.PieceType.KNIGHT -> boardString.append(" N ");
                case ChessPiece.PieceType.ROOK -> boardString.append(" R ");
                case ChessPiece.PieceType.BISHOP -> boardString.append(" B ");
                case ChessPiece.PieceType.QUEEN -> boardString.append(" Q ");
                case ChessPiece.PieceType.KING -> boardString.append(" K ");
            }
        } else {
            boardString.append("   ");
        }
        return boardString.toString();
    }

    public String getGameString() {
        return printBoard(isWhite, false, null);
    }

    public String getGameStringHighlighted(ChessPosition position) {
        return printBoard(isWhite, true, position);
    }

    private int numFlip(int rowCol) {
        if (isWhite) {
            return rowCol;
        } else {
            return 9 - rowCol;
        }
    }

    private String squareColor(int row, int col, ChessPosition position, Collection<ChessMove> validMoves, boolean highlightMoves) {
        if (highlightMoves) {
            if (numFlip(position.getRow()) == row && numFlip(position.getColumn()) == col) {
                return SET_BG_COLOR_YELLOW;
            }
        }
        int flippedCol = numFlip(col);
        int flippedRow = numFlip(row);
        if (row % 2 == 0) {
            if (col % 2 == 0) {
                return highlightSquareColor(highlightMoves, validMoves, SET_BG_COLOR_DARK_TAN, SET_BG_COLOR_BLACK, flippedRow, flippedCol);
            } else {
                return highlightSquareColor(highlightMoves, validMoves, SET_BG_COLOR_LIGHT_TAN, SET_BG_COLOR_WHITE, flippedRow, flippedCol);
            }
        } else {
            if (col % 2 == 0) {
                return highlightSquareColor(highlightMoves, validMoves, SET_BG_COLOR_LIGHT_TAN, SET_BG_COLOR_WHITE, flippedRow, flippedCol);
            } else {
                return highlightSquareColor(highlightMoves, validMoves, SET_BG_COLOR_DARK_TAN, SET_BG_COLOR_BLACK, flippedRow, flippedCol);
            }
        }
    }

    public String highlightSquareColor(boolean highlightMoves, Collection<ChessMove> validMoves,
                                       String mainColor, String alternativeColor, int row, int col) {
        if (!highlightMoves) {
            return mainColor;
        } else {
            for (ChessMove validMove : validMoves) {
                if (Objects.equals(validMove.getEndPosition(), new ChessPosition(row, col))) {
                    return alternativeColor;
                }
            }
        }
        return mainColor;
    }

    public String initGameClientAuth() {
        return authToken;
    }

    public int initGameClientID() {
        return gameID;
    }
}
