package ui;

import chess.*;
import model.*;
import ui.WebSocketFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GameClient {
    private WebSocketFacade wsFacade;
    private GameUI gameui;
    private String authToken;
    private Integer gameID;
    private Boolean promotionSpecialCase = false;
    private ChessMove promotionMoveQueue;

    public GameClient(WebSocketFacade webSocketFacade, GameUI gameUI) {
        wsFacade = webSocketFacade;
        gameui = gameUI;
    }

    public GameClientInitialize(String auth, int id, ) {
        authToken = auth;
        gameID = id;
        gameui = new GameUI();
    }

    public void setGameClientTeam(Boolean isBlack) {
        gameui.updateTeam(isBlack);
    }


    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (promotionSpecialCase) {
                return makeMovePromotionCase(params);
            }
            return switch (cmd) {
                case "leave" -> leave();
                case "game" -> gameHelp();
                case "redraw" -> redraw();
                case "make" -> makeMove(params);
                case "resign" -> resign();
                //case "highlight" -> highlightLegalMoves(params);
                default -> helpCommands();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String helpCommands() {
        return (SET_TEXT_COLOR_CYAN + "game help" + SET_TEXT_COLOR_WHITE + " - to display what actions you can take within the game \n" +
                SET_TEXT_COLOR_CYAN + "redraw" +  SET_TEXT_COLOR_WHITE + " - to redraw the chess board \n" +
                SET_TEXT_COLOR_CYAN + "leave" + SET_TEXT_COLOR_WHITE + " - to leave the game \n" +
                SET_TEXT_COLOR_CYAN + "help" + SET_TEXT_COLOR_WHITE + " - to get help on these commands outside the game \n" +
                SET_TEXT_COLOR_CYAN + "make move" + SET_TEXT_COLOR_WHITE + " - to make a particular move within the game \n" +
                SET_TEXT_COLOR_CYAN + "resign" + SET_TEXT_COLOR_WHITE + " - to resign \n" +
                SET_TEXT_COLOR_CYAN + "highlight legal moves" + SET_TEXT_COLOR_WHITE + " - highlight the legal moves a selected piece can make \n"
                );
    }

    public String gameHelp() {
        return "Here's some help"; // NOT DONE
    }

    public String redraw() {
        return gameui.getGameString();
    }

    public String resign() {
        return "Resigning..."; // NOT DONE
    }

    public String makeMove(String... params) throws ResponseException {
        inputFilter(params.length, 2); // "move" and then the actual move
        try {
            ChessMove newMove = inputToChessMove(params[1]);
            wsFacade.makeMove(authToken, gameID, newMove);
            return "Making move...";
        } catch (ResponseException e) {
            throw new ResponseException(400, e.getMessage());
        } catch (Exception e) {
            return "What would you like to promote your piece to? (queen, rook, bishop, or knight)";
        }
    }

    public String makeMovePromotionCase(String... params) throws ResponseException {
        inputFilter(params.length, 1);
        ChessGame tempGame = gameui.getGame();
        ChessMove promotionMove = inputToPromotionMove(params[0]);
        promotionSpecialCase = false;
        promotionMoveQueue = null;
        try {
            tempGame.makeMove(promotionMove);
            wsFacade.makeMove(authToken, gameID, promotionMove);
            return "Successfully made promotion move";
        } catch (InvalidMoveException e) {
            throw new ResponseException(400, "Error: " + e.getMessage() + "\nPut in a new move or command");
        }
    }

    public ChessMove inputToPromotionMove(String param) {
        var type = switch (param) {
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            default -> null;
        }; // makeMove SHOULD throw the error and otherwise infinite loops will be a problem
        return new ChessMove(promotionMoveQueue.getStartPosition(), promotionMoveQueue.getEndPosition(), type);
    }

//    public String highlightLegalMoves(String... params) throws ResponseException {
//        inputFilter(params.length,3); // "legal moves" and then the actual position of the piece
//        try {
//            serverFacade.register(null); // TODO: This is wrong of course
//            return "hehehe silly";
//        } catch (ResponseException e) {
//            throw new ResponseException(500, "Error: Invalid piece");
//        }
//    }

    public String leave() {
        return "leaving...";
    }

    private void inputFilter(int length, int desiredLength) throws ResponseException {
        if (length > desiredLength) {
            throw new ResponseException(400, "Error: Too many arguments given");
        } else if (length < desiredLength) {
            throw new ResponseException(400, "Error: Too few arguments given");
        }
    }

    // I am going to hope and pray that this is just "e4e6" format
    private ChessMove inputToChessMove(String param) throws Exception {
        ChessGame tempGame = gameui.getGame();
        ChessMove proposedMove;
        proposedMove = parseChessMove(param);
        if (promotionFlagCheck(proposedMove.getEndPosition(), tempGame.getBoard().getPiece(proposedMove.getStartPosition()))) {
            promotionSpecialCase = true;
            promotionMoveQueue = proposedMove;
            throw new Exception();
        }
        try {
            tempGame.makeMove(proposedMove); // is the move valid?
            return proposedMove;
        } catch (InvalidMoveException e) {
            throw new ResponseException(400, "Error: " + e.getMessage());
        }
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

    public boolean promotionFlagCheck(ChessPosition newPosition, ChessPiece piece) {
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                return (newPosition.getRow() == 1);
            } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return (newPosition.getRow() == 8);
            }
        }
        return false;
    }
}