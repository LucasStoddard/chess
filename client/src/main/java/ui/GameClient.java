package ui;

import chess.*;
import model.*;
import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GameClient {
    private WebSocketFacade wsFacade;
    private GameUI gameui;
    private String authToken;
    private Integer gameID;
    private Boolean setUpFlag = false;

    public GameClient(WebSocketFacade webSocketFacade, GameUI gameUI) {
        wsFacade = webSocketFacade;
        gameui = gameUI;
    }

    public void gameClientInitialize(String auth, int id) {
        authToken = auth;
        gameID = id;
    }

    public void setGameClientTeam(Boolean isWhite) {
        gameui.updateTeam(isWhite);
    }


    public String eval(String input) {
        if (!setUpFlag) {
            gameClientInitialize(gameui.initGameClientAuth(), gameui.initGameClientID());
            setUpFlag = true;
        }
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "leave" -> leave();
                case "redraw" -> redraw();
                case "make" -> makeMove(params);
                case "resign" -> resign();
                case "highlight" -> highlightLegalMoves(params);
                default -> helpCommands();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String helpCommands() {
        return (SET_TEXT_COLOR_CYAN + "help" + SET_TEXT_COLOR_WHITE + " - to get help on these commands \n" +
                SET_TEXT_COLOR_CYAN + "redraw" +  SET_TEXT_COLOR_WHITE + " - to redraw the chess board \n" +
                SET_TEXT_COLOR_CYAN + "leave" + SET_TEXT_COLOR_WHITE + " - to leave the game \n" +
                SET_TEXT_COLOR_CYAN + "make move <MOVE> <PROMOTION_PIECE>" + SET_TEXT_COLOR_WHITE +
                " - to make a particular move within the game \nIf your piece can be promoted, put the piece you would like to promote it to" +
                " at the end\n" +
                SET_TEXT_COLOR_CYAN + "resign" + SET_TEXT_COLOR_WHITE + " - to resign \n" +
                SET_TEXT_COLOR_CYAN + "highlight legal moves <POSITION>" + SET_TEXT_COLOR_WHITE +
                " - highlight the legal moves a selected piece can make"
                );
    }

    public String redraw() {
        return gameui.getGameString();
    }

    public String resign() throws ResponseException {
        try {
            wsFacade.resignGame(authToken, gameID);
            return "Resigning...";
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public String makeMove(String... params) throws ResponseException {
        ChessMove newMove;
        try {
            if (params.length == 2) {
                newMove = parseChessMove(params[1]);
            } else if (params.length == 3) {
                newMove = inputToPromotionMove(parseChessMove(params[1]), params[2]);
            } else {
                throw new ResponseException(400, "Error: Invalid move");
            }

            wsFacade.makeMove(authToken, gameID, newMove);
            return "Making move...";
        } catch (ResponseException e) {
            throw new ResponseException(400, e.getMessage());
        } catch (Exception e) {
            throw new ResponseException(400, e.getMessage());
            //return "What would you like to promote your piece to? (queen, rook, bishop, or knight)";
        }
    }

    public ChessPosition parseChessPosition(String param) throws ResponseException{
        String[] moveArray = param.split("");
        if (moveArray.length != 2) {
            throw new ResponseException(400, "Error: Invalid move");
        }
        int sPosRow = letterToRowCol(moveArray[0], true);
        int sPosCol = letterToRowCol(moveArray[1], false);
        return new ChessPosition(sPosCol, sPosRow);
    }

    public String highlightLegalMoves(String... params) throws ResponseException {
        inputFilter(params.length,3); // "legal moves" and then the actual position of the piece
        try {
            return gameui.getGameStringHighlighted(parseChessPosition(params[2]));
        } catch (ResponseException e) {
            throw new ResponseException(500, "Error: Invalid piece");
        }
    }

    public String leave() throws ResponseException{
        try {
            wsFacade.leaveGame(authToken, gameID);
            return "Leaving...";
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void inputFilter(int length, int desiredLength) throws ResponseException {
        if (length > desiredLength) {
            throw new ResponseException(400, "Error: Too many arguments given");
        } else if (length < desiredLength) {
            throw new ResponseException(400, "Error: Too few arguments given");
        }
    }

    public ChessMove inputToPromotionMove(ChessMove oldMove, String param) {
        var type = switch (param) {
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            default -> null;
        };
        return new ChessMove(oldMove.getStartPosition(), oldMove.getEndPosition(), type);
    }

    public ChessMove parseChessMove(String param) throws ResponseException {
        String[] moveArray = param.split("");
        if (moveArray.length != 4) {
            throw new ResponseException(400, "Error: Invalid move");
        }
        int sPosRow = letterToRowCol(moveArray[0], true);
        int sPosCol = letterToRowCol(moveArray[1], false);
        int ePosRow = letterToRowCol(moveArray[2], true);
        int ePosCol = letterToRowCol(moveArray[3], false);
        ChessPosition sPosition = new ChessPosition(sPosRow, sPosCol);
        ChessPosition ePosition = new ChessPosition(ePosRow, ePosCol);
        return new ChessMove(sPosition, ePosition, null);
    }

    private int letterToRowCol(String numLetter, boolean isRow) throws ResponseException {
        if (isRow) {
            return switch (numLetter) {
                case "a" -> 1;
                case "b" -> 2;
                case "c" -> 3;
                case "d" -> 4;
                case "e" -> 5;
                case "f" -> 6;
                case "g" -> 7;
                case "h" -> 8;
                default -> throw new ResponseException(400, "Error: Invalid move");
            };
        } else {
            return switch (numLetter) {
                case "1" -> 1;
                case "2" -> 2;
                case "3" -> 3;
                case "4" -> 4;
                case "5" -> 5;
                case "6" -> 6;
                case "7" -> 7;
                case "8" -> 8;
                default -> throw new ResponseException(400, "Error: Invalid move");
            };
        }
    }
}