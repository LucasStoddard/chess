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
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (type == PieceType.BISHOP) { // first I will ignore the pieces on the board, then I will change this code to consider those pieces
            int realRowStart = myPosition.getRow() - 1;
            int realColStart = myPosition.getColumn() - 1;
            int tempRow = realRowStart;
            int tempCol = realColStart;
            // up right, down right, down left, up left
            while (tempCol < 7 && tempRow < 7 && tempCol > 0 && tempRow > 0) {
                tempRow++;
                tempCol++;
                moves.add(new ChessMove(myPosition, new ChessPosition(tempRow+1, tempCol+1), null));
            }
            tempRow = realRowStart;
            tempCol = realColStart;
            // down right
            while (tempCol < 7 && tempRow < 7 && tempCol > 0 && tempRow > 0) {
                tempRow--;
                tempCol++;
                moves.add(new ChessMove(myPosition, new ChessPosition(tempRow+1, tempCol+1), null));
            }
            tempRow = realRowStart;
            tempCol = realColStart;
            // down left
            while (tempCol < 7 && tempRow < 7 && tempCol > 0 && tempRow > 0) {
                tempRow--;
                tempCol--;
                moves.add(new ChessMove(myPosition, new ChessPosition(tempRow+1, tempCol+1), null));
            }
            tempRow = realRowStart;
            tempCol = realColStart;
            // up left
            while (tempCol < 7 && tempRow < 7 && tempCol > 0 && tempRow > 0) {
                tempRow++;
                tempCol--;
                moves.add(new ChessMove(myPosition, new ChessPosition(tempRow+1, tempCol+1), null));
            }
        }
        return moves;
    }
}
