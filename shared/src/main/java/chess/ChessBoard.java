package chess;

import java.util.Arrays;
import java.util.Objects;
import java.util.HashMap;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    // IMPORTANT: Chessboard is indexed from bottom left 0-7 for rows, and then left to right 0-7 for columns
    // BUT - moves are tested as if this is not zero indexed (1-8) for both rows and columns
    private ChessPiece[][] squares;
    private HashMap<Integer, ChessPiece> pieceTypeNumbers = new HashMap<>() {{ // hashmap for easy board initialization
        put(1, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)); put(2, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT)); put(3, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        put(4, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK)); put(5, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN)); put(6, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        put(7, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)); put(8, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT)); put(9, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        put(10, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK)); put(11, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN)); put(12, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        put(0, null);
    }};
    private final int[] defaultBoard = {4,2,3,5,6,3,2,4,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,7,7,7,7,7,7,7,7,10,8,9,11,12,9,8,10};

    public ChessBoard() {
        this.squares = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1]; // Uninitialized array is populated with null so no need to check
    }

    /**
     * Checks if a piece exists at a certain location
     *
     * @param position The position to get the piece from
     * @return true or false if there is a piece in that position
     */
    public boolean ifPiece(ChessPosition position) {
        if (position.getRow() > 8 || position.getRow() < 1 || position.getColumn() > 8 || position.getColumn() < 1) {
            return false;
        } else if (getPiece(position) == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = pieceTypeNumbers.get(defaultBoard[i*8+j]);
            }
        }
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
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
    // end generated code
}
