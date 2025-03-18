import chess.*;


public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
    }
}

// TODO: Make prelogin Repl, the steps I assume we must accomplish (in no order) are:
//      0. Create the server facade
//      1. Code the eval loop
//      2. Code the helper functions which give responses according to errors
//      3. Code the help part
//      4. Implement the Repl into our main
//      5. Make it so Login passes to Main, and then repeat the process for main and game