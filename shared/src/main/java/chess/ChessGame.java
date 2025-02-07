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
    private boolean[][] enPassantCheck;
    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        enPassantCheck = new boolean[8][8];
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
    public ArrayList<PieceAndMoves> complexMoves(ChessBoard thisBoard, TeamColor team) {
        ArrayList<PieceAndMoves> tempList = new ArrayList<>();
        ChessPiece tempPiece = null;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if ((tempPiece = thisBoard.getPiece(new ChessPosition(i,j))) != null) {
                    if (tempPiece.getTeamColor() == team) {
                        tempList.add(new PieceAndMoves(tempPiece, tempPiece.pieceMoves(chessBoard, new ChessPosition(i, j))));
                    }
                }
            }
        }
        return tempList;
    }

    public ChessPosition getKingPosition(TeamColor team, ChessBoard board) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece tempPiece = board.getPiece(new ChessPosition(i,j));
                if (tempPiece != null) {
                    if (tempPiece.getPieceType() == ChessPiece.PieceType.KING && tempPiece.getTeamColor() == team) return new ChessPosition(i,j);
                }
            }
        }
        return null;
    }

    public ChessPiece[][] getDuplicateBoard(ChessBoard board) {
        ChessPiece[][] copy = new ChessPiece[8][8];
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                copy[i-1][j-1] = board.getPiece(new ChessPosition(i,j));
            }
        }
        return copy;
    }


    public boolean kingCaptureIfMove(ChessMove move, ChessPiece piece) {
        ChessPiece[][] oldBoardSquares = getDuplicateBoard(chessBoard);
        ChessPiece[][] simulatedChessSquares = getDuplicateBoard(chessBoard);
        if (move != null) {
            ChessPosition sPos = move.getStartPosition();
            ChessPosition ePos = move.getEndPosition();
            simulatedChessSquares[sPos.getRow() - 1][sPos.getColumn() - 1] = null;
            simulatedChessSquares[ePos.getRow() - 1][ePos.getColumn() - 1] = piece;
        }
        ChessBoard simulatedChessBoard = new ChessBoard(simulatedChessSquares);
        setBoard(simulatedChessBoard);
        ArrayList<PieceAndMoves> potentialCaptures = complexMoves(simulatedChessBoard, invertTeam(piece.getTeamColor()));
        ChessPosition kingPos = getKingPosition(piece.getTeamColor(), simulatedChessBoard);
        for (int i = 0; i < potentialCaptures.size(); i++) {
            Collection<ChessMove> tempMoves = potentialCaptures.get(i).getMoves();
            for (ChessMove specificMoves : tempMoves) {
                if (specificMoves.getEndPosition().getRow() == kingPos.getRow() && specificMoves.getEndPosition().getColumn() == kingPos.getColumn()) {
                    setBoard(new ChessBoard(oldBoardSquares));
                    return true;
                }
            }
        }
        setBoard(new ChessBoard(oldBoardSquares));
        return false;
    }


    //     { row , col }
    //        + row
    //           ^
    // - col  ←     → + col
    //           v
    //         - row
    Collection<ChessMove> enPassantMoves (ChessPosition sPos) {
        ArrayList<ChessMove> tempList = new ArrayList<>();
        ChessPiece tempPiece = chessBoard.getPiece(sPos);
        if (tempPiece == null) {
            return tempList;
        } else if (tempPiece.getPieceType() != ChessPiece.PieceType.PAWN) {
            return tempList;
        }
        int rowMove = 0;
        if (tempPiece.getTeamColor() == TeamColor.BLACK && sPos.getRow() == 4) {
            rowMove = -1;
        } else if (tempPiece.getTeamColor() == TeamColor.WHITE && sPos.getRow() == 5) {
            rowMove = 1;
        } else {
            return tempList;
        }
        boolean leftCheck = (sPos.getColumn() > 1);
        boolean rightCheck = (sPos.getColumn() < 8);
        if (leftCheck && enPassantCheck[sPos.getRow()-1][sPos.getColumn()-2]) { // left check
            tempList.add(new ChessMove(sPos, new ChessPosition(sPos.getRow() + rowMove, sPos.getColumn() - 1), null));
        }
        if (rightCheck && enPassantCheck[sPos.getRow()-1][sPos.getColumn()] ) { // right check
            tempList.add(new ChessMove(sPos, new ChessPosition(sPos.getRow() + rowMove, sPos.getColumn() + 1), null));
        }
        return tempList;
    }

    // Collection<ChessMove> castlingMoves (ChessPosition sPos)


    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ArrayList<ChessMove> filteredMoves = new ArrayList<>();
        ChessPiece pieceToBeMoved = chessBoard.getPiece(startPosition);
        if (pieceToBeMoved == null) return filteredMoves;
        Collection<ChessMove> unfilteredMoves = pieceToBeMoved.pieceMoves(chessBoard, startPosition);
        unfilteredMoves.addAll(enPassantMoves(startPosition));
        // unfilteredMoves.addAll(CastlingMoves(startPosition));
        for (ChessMove evaluatedMove : unfilteredMoves) {
            if (!kingCaptureIfMove(evaluatedMove, pieceToBeMoved)) {
                filteredMoves.add(evaluatedMove);
            }
        }
        return filteredMoves;
    }

    boolean enPassantMoveHelper(ChessPosition sPos, ChessPosition ePos) {
        if (sPos.getRow() != ePos.getRow() && sPos.getColumn() != ePos.getColumn() && chessBoard.getPiece(ePos) == null) { // if diagonal move to empty square
            return true;
        }
        return false;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece tempPiece = chessBoard.getPiece(move.getStartPosition());
        ChessPosition sPos = move.getStartPosition();
        ChessPosition ePos = move.getEndPosition();
        if (tempPiece == null) {
            throw new InvalidMoveException("No piece at that starting position");
        } else if (sPos.getRow() == ePos.getRow() && sPos.getColumn() == ePos.getColumn()) {
            throw new InvalidMoveException("Starting position and ending position are the same");
        } else if (tempPiece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("It is not that team's turn");
        }
        Collection<ChessMove> validMoves = validMoves(sPos);
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        } else {
            if (move.getPromotionPiece() != null) {
                chessBoard.addPiece(ePos, new ChessPiece(tempPiece.getTeamColor(), move.getPromotionPiece()));
            } else {
                if (tempPiece.getPieceType() == ChessPiece.PieceType.PAWN) { // en passant stuff
                    if (enPassantMoveHelper(sPos, ePos)) { // ep capture
                        chessBoard.addPiece(new ChessPosition(sPos.getRow(), ePos.getColumn()), null);
                    }
                    if (sPos.getRow() - ePos.getRow() == 2 || sPos.getRow() - ePos.getRow() == -2) {
                        enPassantCheck[ePos.getRow() - 1][ePos.getColumn() - 1] = true;
                    } else {
                        enPassantCheck = new boolean[8][8];
                    }
                }
                chessBoard.addPiece(ePos, tempPiece);
            }
            chessBoard.addPiece(sPos, null);
            setTeamTurn(invertTeam(teamTurn));
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        if (kingCaptureIfMove(null, chessBoard.getPiece(getKingPosition(teamColor, chessBoard)))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        for (PieceAndMoves tempPieceAndMoves : complexMoves(chessBoard, teamColor)) {
            Collection<ChessMove> pieceMoves = tempPieceAndMoves.getMoves();
            for (ChessMove individualMoves : pieceMoves) {
                if (!kingCaptureIfMove(individualMoves, tempPieceAndMoves.getPieceDetail())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            for (PieceAndMoves tempPieceAndMoves : complexMoves(chessBoard, teamColor)) {
                Collection<ChessMove> pieceMoves = tempPieceAndMoves.getMoves();
                for (ChessMove individualMoves : pieceMoves) {
                    if (validMoves(individualMoves.getStartPosition()).contains(individualMoves)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
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
