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
                case "logout" -> quit();
//                case "create" -> create(params);
//                case "list" -> list(params);
//                case "play" -> play(params);
//                case "observe" -> observe(params);
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String help() { // It required a lot of testing but this should work
        return (SET_TEXT_COLOR_CYAN + "register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_DARK_GREY + " - to create an account \n" +
                SET_TEXT_COLOR_CYAN + "login <USERNAME> <PASSWORD>" +  SET_TEXT_COLOR_DARK_GREY + " - to login to an account \n" +
                SET_TEXT_COLOR_CYAN + "quit" + SET_TEXT_COLOR_DARK_GREY + " - yeah you know what this does \n" +
                SET_TEXT_COLOR_DARK_GREY + "help" + SET_TEXT_COLOR_DARK_GREY + " - get some help \n");
    }

    public String quit() {
        return "quit";
    }
}
