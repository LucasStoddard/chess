package ui;

import model.ResponseException;
import server.ServerFacade;
import ui.EscapeSequences.*;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class LoginClient {
    private String url;
    private ServerFacade serverFacade;

    public LoginClient(String serverUrl) {
        url = serverUrl;
        serverFacade = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> quit();
                case "login" -> login(params);
                case "register" -> register(params);
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String help() {
        return (SET_TEXT_COLOR_CYAN + "register <USERNAME> <PASSWORD> <EMAIL>" +
                SET_TEXT_COLOR_DARK_GREY + " - to create an account \n" +
                SET_TEXT_COLOR_CYAN + "login <USERNAME> <PASSWORD>" +
                SET_TEXT_COLOR_DARK_GREY + " - to login to an account \n" +
                SET_TEXT_COLOR_CYAN + "quit" + SET_TEXT_COLOR_DARK_GREY + " - yeah you know what this does \n" +
                SET_TEXT_COLOR_DARK_GREY + "help" + SET_TEXT_COLOR_DARK_GREY + " - get some help \n");
    }
}
