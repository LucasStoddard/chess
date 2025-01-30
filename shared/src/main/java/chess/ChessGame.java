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
    public class PieceMoves {
        private ChessPiece piece;
        private Collection<ChessMove> moves;
        public PieceMoves(ChessPiece piece, Collection<ChessMove> moves) {
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

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
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


    /**
     * Gets either only the capturing moves or all the moves.
     * The capturing moves of the opposing team are needed in order to determine valid king moves,
     * as moving a king into check is not allowed, thus this method is needed. The addition of returning
     * all the normal moves is helpful because it gives a list which can be filtered by validMoves in
     * order to get a list to check against for makeMove.
     * NOTE: Capture only isn't just capturing allowed moves, but also theoretical capture moves. A pawn cannot move
     * diagonally if there is no piece there, but a King could also not move into that position because
     * that would put the king in danger.
     *
     * @return list of format {{ChessPiece, {moves}}, {ChessPiece, {moves}}, {ChessPiece, {moves}}...}
     */
    public ArrayList<ArrayList<PieceMoves>> complexMoves(TeamColor team, boolean captureOnly) { // CURRENT SPOT: Just finished making the PieceMoves type for this function
        throw new RuntimeException("Not implemented");
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
