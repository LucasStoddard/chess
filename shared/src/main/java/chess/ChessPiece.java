package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor team;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.team = pieceColor;
        this.type = type;
    }

    // generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return team == that.team && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, type);
    }
    // end generated code

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return team;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates and returns the moves in a consistent direction until the edge of the board or another piece is hit
     * If the piece can be captured then it also returns that as a valid move
     * Directions is given as [+1, +1] meaning up and right, aka increasing columns and rows with each move
     * Also can be used as [+1, 0] which is just up, meaning this can be used for Queen, Bishop, and Rook movement
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> moveUntilEdgeOrPiece(ChessBoard board, ChessPosition myPosition, int[] rowCol, ChessGame.TeamColor color, boolean oneMoveFlag) {
        ArrayList<ChessMove> tempMoves = new ArrayList<>();
        int tempRow = myPosition.getRow() - 1;
        int tempCol = myPosition.getColumn() - 1;
        while (true) {
            if (tempRow + rowCol[0] > 7 || tempRow + rowCol[0] < 0 || tempCol + rowCol[1] > 7 || tempCol + rowCol[1] < 0) { // does it hit the wall?
                break; // hahaha this is a literal edge case
            }
            ChessPiece tempPiece = board.getPiece(new ChessPosition(tempRow + rowCol[0] + 1, tempCol + rowCol[1] + 1));
            if (tempPiece != null) { // does it hit a piece?
                if (tempPiece.getTeamColor() == color) {
                    break; // do not capture your own piece
                } else {  // CURRENT ISSUE: If a pawn can go diagonally and promote as well as just normally and promote only the diagonal is added?
                    if (board.getPiece(myPosition).getPieceType() != PieceType.PAWN && Math.abs(rowCol[0] + rowCol[1]) != 1) {
                        if ((tempRow + rowCol[0] == 0 || tempRow + rowCol[0] == 7) && board.getPiece(myPosition).getPieceType() == PieceType.PAWN) {
                            tempMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow + rowCol[0] + 1, tempCol + rowCol[1] + 1), PieceType.QUEEN));
                            tempMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow + rowCol[0] + 1, tempCol + rowCol[1] + 1), PieceType.ROOK));
                            tempMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow + rowCol[0] + 1, tempCol + rowCol[1] + 1), PieceType.BISHOP));
                            tempMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow + rowCol[0] + 1, tempCol + rowCol[1] + 1), PieceType.KNIGHT));
                        } else {
                            tempMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow + rowCol[0] + 1, tempCol + rowCol[1] + 1), null));
                        }
                        }
                    break; // you can capture enemy piece
                }
            } else {
                tempRow += rowCol[0];
                tempCol += rowCol[1];
                if ((tempRow == 0 || tempRow == 7) && board.getPiece(myPosition).getPieceType() == PieceType.PAWN) {
                    tempMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow + 1, tempCol + 1), PieceType.QUEEN));
                    tempMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow + 1, tempCol + 1), PieceType.ROOK));
                    tempMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow + 1, tempCol + 1), PieceType.BISHOP));
                    tempMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow + 1, tempCol + 1), PieceType.KNIGHT));
                } else {
                    tempMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow + 1, tempCol + 1), null));
                }
            }
            if (oneMoveFlag) {
                break;
            }
        }
        return tempMoves;
    }
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //     { row , col }
        //        + row
        //           ^
        // - col  ←     → + col
        //           v
        //         - row
        ArrayList<ChessMove> moves = new ArrayList<>();
        int[] upRight = {1,1}; int[] downRight = {-1,1}; int[] downLeft = {-1,-1}; int[] upLeft = {1,-1};
        int[] up = {1,0}; int[] down = {-1, 0}; int[] right = {0, -1}; int[] left = {0, 1};
        int[] knightUpRight = {2, 1}, knightUpLeft = {2, -1}, knightDownRight = {-2, 1}, knightDownLeft = {-2, -1};
        int[] knightRightUp = {1, 2}, knightRightDown = {1, -2}, knightLeftUp = {-1, 2}, knightLeftDown = {-1, -2};
        int[] whitePawnUp = {2,0}; int[] blackPawnDown = {-2, 0};
        if (type == PieceType.BISHOP) { // I know this could be condensed, but I am keeping it this way (for now) for readability and my sanity
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, upRight, team, false));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, downRight, team, false));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, downLeft, team, false));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, upLeft, team, false));
        } else if (type == PieceType.ROOK) {
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, up, team, false));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, down, team, false));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, left, team, false));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, right, team, false));
        } else if (type == PieceType.QUEEN || type == PieceType.KING) {
            boolean isKing = (type == PieceType.KING);
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, upRight, team, isKing));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, downRight, team, isKing));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, downLeft, team, isKing));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, upLeft, team, isKing));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, up, team, isKing));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, down, team, isKing));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, left, team, isKing));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, right, team, isKing));
        } else if (type == PieceType.KNIGHT) {
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, knightUpRight, team, true));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, knightUpLeft, team,true));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, knightDownRight, team,true));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, knightDownLeft, team,true));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, knightRightUp, team,true));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, knightRightDown, team,true));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, knightLeftUp, team,true));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, knightLeftDown, team,true));
        } else if (type == PieceType.PAWN) {
            if (team == ChessGame.TeamColor.WHITE) {
                if (myPosition.getRow() == 2) { // first move as pawns cannot go backwards
                    moves.addAll(moveUntilEdgeOrPiece(board, myPosition, whitePawnUp, team, true));
                }
                moves.addAll(moveUntilEdgeOrPiece(board, myPosition, up, team, true));
                if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)) != null) {
                    moves.addAll(moveUntilEdgeOrPiece(board, myPosition, upLeft, team, true));
                }
                if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)) != null) {
                    moves.addAll(moveUntilEdgeOrPiece(board, myPosition, upRight, team, true));
                }
            }
            if (team == ChessGame.TeamColor.BLACK) {
                if (myPosition.getRow() == 7) { // first move as pawns cannot go backwards
                    moves.addAll(moveUntilEdgeOrPiece(board, myPosition, blackPawnDown, team, true));
                }
                moves.addAll(moveUntilEdgeOrPiece(board, myPosition, down, team, true));
                if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)) != null) {
                    moves.addAll(moveUntilEdgeOrPiece(board, myPosition, downLeft, team, true));
                }
                if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)) != null) {
                    moves.addAll(moveUntilEdgeOrPiece(board, myPosition, downRight, team, true));
                }
            }
        }
        return moves;
    }
}
