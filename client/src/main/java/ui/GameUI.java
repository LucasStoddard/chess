package ui;

import chess.*;
import static ui.EscapeSequences.*;

public class GameUI {
    ChessBoard board;

    public GameUI() {
        board = new ChessBoard();
        board.resetBoard();
    }

    private String printBoard(boolean isReversed) {
        StringBuilder boardString = new StringBuilder();
        if (isReversed) {
            boardString.append(firstRow(true));
            for (int i = 8; i > 0; i--) {
                boardString.append(otherRow(i, true));
            }
            boardString.append(firstRow(true));
        } else {
            boardString.append(firstRow(false));
            for (int i = 1; i < 9; i++) {
                boardString.append(otherRow(i, false));
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

    private String otherRow(int row, boolean isReversed) {
        StringBuilder boardString = new StringBuilder();
        boardString.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_CYAN);
        boardString.append(" %d ".formatted(row));
        if (isReversed) {
            for (int i = 1; i < 9; i++) {
                boardString.append(squareColor(row, i));
                boardString.append(piece(row, i));
            }
        } else {
            for (int i = 8; i > 0; i--) {
                boardString.append(squareColor(row, i));
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
    public String getGameStringBothSides() {
        return printBoard(false) + "\n" + printBoard(true);
    }

    private String squareColor(int row, int col) {
        if (row % 2 == 0) {
            if (col % 2 == 0) {
                return SET_BG_COLOR_DARK_TAN;
            } else {
                return SET_BG_COLOR_LIGHT_TAN;
            }
        } else {
            if (col % 2 == 0) {
                return SET_BG_COLOR_LIGHT_TAN;
            } else {
                return SET_BG_COLOR_DARK_TAN;
            }
        }
    }
}
