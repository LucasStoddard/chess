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
     * @return true if this is a pawn that can be promoted here
     */
    public boolean promotionFlag(ChessPosition newPosition) {
        if (type == PieceType.PAWN) {
            if (team == ChessGame.TeamColor.BLACK) {
                return (newPosition.getRow() == 1);
            } else if (team == ChessGame.TeamColor.WHITE) {
                return (newPosition.getRow() == 8);
            }
        }
        return false;
    }

    /**
     * @return if the piece can capture here
     */
    public boolean canCapture(ChessBoard board, ChessPosition newPosition, int rowMove, int colMove) {
        if (type != PieceType.PAWN) { // every other piece can capture if the pieces are different
            return (team != board.getPiece(newPosition).getTeamColor());
        } else {
            if (rowMove + colMove == 0 || rowMove + colMove == 2 || rowMove + colMove == -2) { // if pawn is moving diagonally, it can capture
                return (team != board.getPiece(newPosition).getTeamColor());
            } else { // otherwise pawn cannot capture
                return false;
            }
        }
    }
    /**
     * Calculates and returns the moves in a consistent direction until the edge of the board or another piece is hit
     * If the piece can be captured then it also returns that as a valid move
     * Directions is given as [+1, +1] meaning up and right, aka increasing columns and rows with each move
     * Also can be used as [+1, 0] which is just up, meaning this can be used for Queen, Bishop, and Rook movement
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> moveUntilEdgeOrPiece(ChessBoard board, ChessPosition myPosition, int[][] allMoves, boolean oneMoveFlag) {
        ArrayList<ChessMove> tempMoves = new ArrayList<>();
        for (int i = 0; i < allMoves.length; i++) { // for all the moves in the array
            int tempRow = myPosition.getRow() - 1;
            int tempCol = myPosition.getColumn() - 1;
            int rowMove = allMoves[i][0];
            int colMove = allMoves[i][1];
            while (true) { // continue in one direction until stopped or one move completed
                if (tempRow + rowMove + 1 > 8 || tempRow + rowMove + 1 < 1 || tempCol + colMove + 1 > 8 || tempCol + colMove + 1 < 1) { // OOB check
                    break;
                }
                ChessPosition newPosition = new ChessPosition(tempRow + rowMove + 1, tempCol + colMove + 1);
                if (board.ifPiece(newPosition) && canCapture(board, newPosition, rowMove, colMove)) { // if there's a piece and you can capture it
                    if (promotionFlag(newPosition)) { // queen, rook, bishop, knight
                        tempMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                        tempMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                        tempMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                        tempMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                        break;
                    } else {
                        tempMoves.add(new ChessMove(myPosition, newPosition, null));
                        break;
                    }
                } else if (board.ifPiece(newPosition)) { // if there's a piece, and you can't capture it
                    break;
                } else { // if there's no piece, then you can move
                    if (promotionFlag(newPosition)) {
                        tempMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                        tempMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                        tempMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                        tempMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                    } else {
                        tempMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
                if (oneMoveFlag) {
                    break;
                } else {
                    tempRow += rowMove;
                    tempCol += colMove;
                }
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

        int[][] bishopMoves = {upRight, downRight, downLeft, upLeft};
        int[][] rookMoves = {up, down, right, left};
        int[][] kingQueenMoves = {upRight, downRight, downLeft, upLeft, up, down, right, left};
        int[][] knightMoves = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
        int[][] whitePawnMove = {up}; int[][] blackPawnMove = {down};
        int[][] whitePawnLeft = {upLeft}; int[][] whitePawnRight = {upRight};
        int[][] blackPawnLeft = {downLeft}; int[][] blackPawnRight = {downRight};
        int[][] whitePawnFirstMove = {{2,0}}; int[][] blackPawnFirstMove = {{-2, 0}};

        if (type == PieceType.BISHOP) { // I know this could be condensed, but I am keeping it this way (for now) for readability and my sanity
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, bishopMoves, false));
        } else if (type == PieceType.ROOK) {
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, rookMoves, false));
        } else if (type == PieceType.QUEEN || type == PieceType.KING) {
            boolean isKing = (type == PieceType.KING);
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, kingQueenMoves, isKing));
        } else if (type == PieceType.KNIGHT) {
            moves.addAll(moveUntilEdgeOrPiece(board, myPosition, knightMoves, true));
        } else if (type == PieceType.PAWN) {
            if (team == ChessGame.TeamColor.WHITE) {
                moves.addAll(moveUntilEdgeOrPiece(board, myPosition, whitePawnMove, true));
                if (board.ifPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1))) {
                    moves.addAll(moveUntilEdgeOrPiece(board, myPosition, whitePawnRight, true));
                }
                if (board.ifPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1))) {
                    moves.addAll(moveUntilEdgeOrPiece(board, myPosition, whitePawnLeft, true));
                }
                if (myPosition.getRow() == 2 && board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null &&
                        board.getPiece(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn())) == null) {
                    moves.addAll(moveUntilEdgeOrPiece(board, myPosition, whitePawnFirstMove, true));
                }
            } else {
                moves.addAll(moveUntilEdgeOrPiece(board, myPosition, blackPawnMove, true));
                if (board.ifPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1))) {
                    moves.addAll(moveUntilEdgeOrPiece(board, myPosition, blackPawnRight, true));
                }
                if (board.ifPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1))) {
                    moves.addAll(moveUntilEdgeOrPiece(board, myPosition, blackPawnLeft, true));
                }
                if (myPosition.getRow() == 7 && board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null &&
                        board.getPiece(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn())) == null) {
                    moves.addAll(moveUntilEdgeOrPiece(board, myPosition, blackPawnFirstMove, true));
                }
            }
        }
        return moves;
    }
}
