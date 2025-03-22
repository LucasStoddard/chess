import chess.*;

import static ui.EscapeSequences.*;


public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
    }
}

// TODO: Make prelogin Repl, the steps I assume we must accomplish (in no order) are:
//      0. Create the server facade (DONE)
//      0.5. Rather than 3 repl files, use 1 repl and 3 clients (DONE)
//      1. Code the eval loop (DONE)
//      2. Code the helper functions which give responses according to errors (DONE)
//      3. Code the help part (DONE)
//      4. Implement the Repl into our main (DONE)
// TODO: Next Phase
//      1. Code MainClient
//      2. Update Repl Loop
//      3. Code GameClient