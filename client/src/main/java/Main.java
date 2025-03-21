import chess.*;

import static ui.EscapeSequences.*;


public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        String uhh = (SET_TEXT_COLOR_CYAN + "register <USERNAME> <PASSWORD> <EMAIL>" +
                SET_TEXT_COLOR_DARK_GREY + " - to create an account \n" +
                SET_TEXT_COLOR_CYAN + "login <USERNAME> <PASSWORD>" +
                SET_TEXT_COLOR_DARK_GREY + " - to login to an account \n" +
                SET_TEXT_COLOR_CYAN + "quit" + SET_TEXT_COLOR_DARK_GREY + " - yeah you know what this does \n" +
                SET_TEXT_COLOR_DARK_GREY + "help" + SET_TEXT_COLOR_DARK_GREY + " - get some help \n");
        System.out.println(uhh);
    }
}

// TODO: Make prelogin Repl, the steps I assume we must accomplish (in no order) are:
//      0. Create the server facade (DONE)
//      0.5. Rather than 3 repl files, use 1 repl and 3 clients (DONE)
//      1. Code the eval loop
//      2. Code the helper functions which give responses according to errors
//      3. Code the help part
//      4. Implement the Repl into our main
//      5. Make it so Login passes to Main, and then repeat the process for main and game