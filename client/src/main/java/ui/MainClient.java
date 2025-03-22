package ui;

import model.ResponseException;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_CYAN;
import static ui.EscapeSequences.SET_TEXT_COLOR_DARK_GREY;

public class MainClient {
    private final ServerFacade serverFacade;

    public MainClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(); // OKAY logout is not possible without websocket so just quit here
//                case "create" -> create(params);
//                case "list" -> list(params);
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
                SET_TEXT_COLOR_CYAN + "create <NAME>" +  SET_TEXT_COLOR_DARK_GREY + " - to create a game \n" +
                SET_TEXT_COLOR_CYAN + "list" + SET_TEXT_COLOR_DARK_GREY + " - to list all the games \n" +
                SET_TEXT_COLOR_CYAN + "join <ID> <WHITE/BLACK>" + SET_TEXT_COLOR_DARK_GREY + " - to join a game by ID on WHITE or BLACK \n" +
                SET_TEXT_COLOR_CYAN + "observe <ID>" + SET_TEXT_COLOR_DARK_GREY + " - to watch a game by ID \n" +
                SET_TEXT_COLOR_CYAN + "help" + SET_TEXT_COLOR_DARK_GREY + " - get some help \n");
    }

    public String logout() {
        return "Successfully logged out";
    }


}
