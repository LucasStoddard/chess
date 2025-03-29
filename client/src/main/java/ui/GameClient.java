package ui;

import model.*;
import server.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GameClient {
    private final ServerFacade serverFacade;

    public GameClient(ServerFacade serverF) {
        serverFacade = serverF;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "leave" -> leave();
                //case "game" -> gamehelp();
                //case "register" -> register(params);
                //case "clear" -> clear();
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

    public String leave() {
        return "leaving...";
    }
}