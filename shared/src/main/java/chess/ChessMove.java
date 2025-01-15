package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    // if there is no promotion, set proPiece to null
    private final ChessPosition sPosition;
    private final ChessPosition ePosition;
    private final ChessPiece.PieceType proPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.sPosition = startPosition;
        this.ePosition = endPosition;
        this.proPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return sPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return ePosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return proPiece;
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
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(sPosition, chessMove.sPosition) && Objects.equals(ePosition, chessMove.ePosition) && proPiece == chessMove.proPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sPosition, ePosition, proPiece);
    }
    // end generated code
}
