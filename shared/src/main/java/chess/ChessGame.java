package chess;

import java.util.Collection;
import java.util.ArrayList;
/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    public class PieceAndMoves {
        private ChessPiece piece;
        private Collection<ChessMove> moves;
        public PieceAndMoves(ChessPiece piece, Collection<ChessMove> moves) {
            this.piece = piece;
            this.moves = moves;
        }
        public ChessPiece getPieceDetail() {
            return piece;
        }
        public void setPieceDetail(ChessPiece piece) {
            this.piece = piece;
        }
        public Collection<ChessMove> getMoves() {
            return moves;
        }
        public void setMoves(Collection<ChessMove> moves) {
            this.moves = moves;
        }
    }
    private TeamColor teamTurn;
    private ChessBoard chessBoard;
    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.chessBoard = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public TeamColor invertTeam(TeamColor team) {
        if (team == TeamColor.BLACK) {
            return TeamColor.WHITE;
        } else {
            return TeamColor.BLACK;
        }
    }

    /**
     * Gets all the moves. These are helpful because it gives a list which can be filtered by validMoves in
     * order to get a list to check against for makeMove.
     * NOTE: This produces moves assuming the King can move into check, validMoves will use this method but then
     * remove the moves that put the King into check.
     *
     * @return list of format {{ChessPiece, {moves}}, {ChessPiece, {moves}}, {ChessPiece, {moves}}...}
     */
    public ArrayList<PieceAndMoves> complexNoCaptureMoves(TeamColor team) {
        ArrayList<PieceAndMoves> tempList = new ArrayList<>();
        ChessPiece tempPiece = null;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if ((tempPiece = chessBoard.getPiece(new ChessPosition(i,j))) != null) {
                    if (tempPiece.getTeamColor() == team) tempList.add(new PieceAndMoves(tempPiece, tempPiece.pieceMoves(chessBoard, new ChessPosition(i,j))));
                }
            }
        }
        return tempList;
    } // TODO: for can capture flag, it may be helpful to temporarily invert the team of each piece (in move generation before reverting it) so that if the King captures a piece that move will exist, this still leaves the diagonal pawn moves to be manually made

    /**
     * Same as complexNoCaptureMoves but does other steps in order to produce all the places where the King could move
     * that would put the King into check.
     *
     * @return list of format {{ChessPiece, {moves}}, {ChessPiece, {moves}}, {ChessPiece, {moves}}...}
     */
    public ArrayList<PieceAndMoves> complexCaptureMoves(TeamColor team) {
        ArrayList<PieceAndMoves> tempList = new ArrayList<>();
        ChessPiece tempPiece = null;
        ChessPiece tempPieceInverted;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if ((tempPiece = chessBoard.getPiece(new ChessPosition(i,j))) != null) {
                    if (tempPiece.getTeamColor() == team && tempPiece.getPieceType() != ChessPiece.PieceType.PAWN) {
                        tempPieceInverted = new ChessPiece(invertTeam(team), tempPiece.getPieceType());
                        tempList.add(new PieceAndMoves(tempPiece, tempPieceInverted.pieceMoves(chessBoard, new ChessPosition(i,j))));
                    }
                }
            }
        }
        return tempList;
    }
    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        chessBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessBoard;
    }
}
