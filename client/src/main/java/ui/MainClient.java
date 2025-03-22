package ui;

import model.*;
import server.ServerFacade;
import java.util.HashSet;
import java.util.Arrays;

import static ui.EscapeSequences.*;

public class MainClient {
    private final ServerFacade serverFacade;

    public MainClient(ServerFacade serverF) {
        serverFacade = serverF;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(); // okay
                case "create" -> create(params);
                case "list" -> list();
//                case "join" -> play(params);
//                case "observe" -> observe(params);
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String help() { // It required a lot of testing but this should work
        return (SET_TEXT_COLOR_CYAN + "logout" + SET_TEXT_COLOR_DARK_GREY + " - to log out \n" +
                SET_TEXT_COLOR_CYAN + "create <NAME>" +  SET_TEXT_COLOR_DARK_GREY + " - to create a game (this does not join you to the game)\n" +
                SET_TEXT_COLOR_CYAN + "list" + SET_TEXT_COLOR_DARK_GREY + " - to list all the games \n" +
                SET_TEXT_COLOR_CYAN + "join <ID> <WHITE/BLACK>" + SET_TEXT_COLOR_DARK_GREY + " - to join a game by ID on WHITE or BLACK \n" +
                SET_TEXT_COLOR_CYAN + "observe <ID>" + SET_TEXT_COLOR_DARK_GREY + " - to watch a game by ID \n" +
                SET_TEXT_COLOR_CYAN + "help" + SET_TEXT_COLOR_DARK_GREY + " - get some help \n");
    }

    public String logout() throws ResponseException {
        try {
            serverFacade.logout(serverFacade.getAuth());
        } catch (ResponseException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public String create(String... params) throws ResponseException { // if errors are here it may be authToken handling
        if (params.length == 2) {
            try {
                GameData game = serverFacade.create(serverFacade.getAuth(), params[0]);
                return String.format("Game named \"%s\" successfully created", game.gameName());
            } catch (ResponseException e) {
                throw new ResponseException(500, e.getMessage());
            }
        } else if (params.length > 2) {
            throw new ResponseException(400, "Too many arguments given");
        } else {
            throw new ResponseException(400, "Too few arguments given");
        }
    }

    public String listPlayerHelper(String playerName) {
        if (playerName == null) {
            return "no player";
        } else {
            return "playerName";
        }
    }

    public String list() throws ResponseException { // if errors are here it may be authToken handling
        try {
            HashSet<GameData> allGames = serverFacade.list(serverFacade.getAuth());
            String allGamesPrintable = "";
            int i = 1;
            for (GameData game : allGames) {
                allGamesPrintable += (i + " " + game.gameName());
                allGamesPrintable += (" White Player: " + listPlayerHelper(game.whiteUsername()));
                allGamesPrintable += (" Black Player: " + listPlayerHelper(game.blackUsername()) + " \n");
            }
            return allGamesPrintable;
        } catch (ResponseException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
