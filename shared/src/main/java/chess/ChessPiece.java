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
    public Collection<ChessMove> moveUntilEdgeOrPiece(ChessBoard board, ChessPosition myPosition, int[] rowCol, ChessGame.TeamColor color) {
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
                } else {
                    tempMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow + rowCol[0] + 1, tempCol + rowCol[1] + 1), null));
                    break; // you can capture enemy piece
                }
            } else {
                tempRow += rowCol[0];
                tempCol += rowCol[1];
                tempMoves.add(new ChessMove(myPosition, new ChessPosition(tempRow + rowCol[0] + 1, tempCol + rowCol[1] + 1), null));
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
        // NOTE: I should really clean up this code, and I need to remake it so that the look ahead doesn't go out of bounds
        // I just most likely need to make a method that is like checkUntil barrier or piece that takes in a direction
        // like (+ +) that returns all the moves that defines, it would make this part many, many lines shorter
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (type == PieceType.BISHOP) { // first I will ignore the pieces on the board, then I will change this code to consider those pieces
            int[] upRight = {1,1}; int[] downRight = {-1,1}; int[] downLeft = {-1,-1}; int[] upLeft = {1,-1};
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, upRight, team));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, downRight, team));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, downLeft, team));
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, upLeft, team));
        }
        return moves;
    }
}
