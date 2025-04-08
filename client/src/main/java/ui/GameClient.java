package ui;

import model.*;
import server.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GameClient {
    private final ServerFacade serverFacade;
    private GameUI gameui;

    public GameClient(ServerFacade serverF) {
        serverFacade = serverF;
        gameui = new GameUI(); // TODO: Uhhh how does the gameUI get the game?
    }

    public void setGameClient(Boolean isBlack) {
        gameui.updateTeam(isBlack);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "leave" -> leave();
                case "game" -> gameHelp();
                case "redraw" -> redraw();
                case "make" -> makeMove(params);
                case "resign" -> resign();
                case "highlight" -> highlightLegalMoves(params);
                default -> helpCommands();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String helpCommands() {
        return (SET_TEXT_COLOR_CYAN + "game help" + SET_TEXT_COLOR_WHITE + " - to display what actions you can take within the game \n" +
                SET_TEXT_COLOR_CYAN + "redraw" +  SET_TEXT_COLOR_WHITE + " - to redraw the chess board \n" +
                SET_TEXT_COLOR_CYAN + "leave" + SET_TEXT_COLOR_WHITE + " - to leave the game \n" +
                SET_TEXT_COLOR_CYAN + "help" + SET_TEXT_COLOR_WHITE + " - to get help on these commands outside the game \n" +
                SET_TEXT_COLOR_CYAN + "make move" + SET_TEXT_COLOR_WHITE + " - to make a particular move within the game \n" +
                SET_TEXT_COLOR_CYAN + "resign" + SET_TEXT_COLOR_WHITE + " - to resign \n" +
                SET_TEXT_COLOR_CYAN + "highlight legal moves" + SET_TEXT_COLOR_WHITE + " - highlight the legal moves a selected piece can make \n"
                );
    }

    public String gameHelp() {
        return "Here's some help"; // NOT DONE
    }

    public String redraw() {
        return gameui.getGameString();
    }

    public String resign() {
        return "Resigning..."; // NOT DONE
    }

    public String makeMove(String... params) throws ResponseException {
        inputFilter(params.length,2); // "move" and then the actual move
        try {
            serverFacade.register(null); // TODO: This is wrong of course
            return "hehehe silly";
        } catch (ResponseException e) {
            throw new ResponseException(500, "Error: Invalid move");
        }
    }

    public String highlightLegalMoves(String... params) throws ResponseException {
        inputFilter(params.length,3); // "legal moves" and then the actual position of the piece
        try {
            serverFacade.register(null); // TODO: This is wrong of course
            return "hehehe silly";
        } catch (ResponseException e) {
            throw new ResponseException(500, "Error: Invalid piece");
        }
    }

    public String leave() {
        return "leaving...";
    }

    public void inputFilter(int length, int desiredLength) throws ResponseException {
        if (length == desiredLength) {
            return;
        } else if (length > desiredLength) {
            throw new ResponseException(400, "Error: Too many arguments given");
        } else {
            throw new ResponseException(400, "Error: Too few arguments given");
        }
    }
}