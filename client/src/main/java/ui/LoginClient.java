package ui;

import model.*;
import server.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class LoginClient {
    private final ServerFacade serverFacade;

    public LoginClient(ServerFacade serverF) {
        serverFacade = serverF;
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
                case "clear" -> clear();
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String help() { // It required a lot of testing but this should work
        return (SET_TEXT_COLOR_CYAN + "register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_WHITE + " - to create an account \n" +
                SET_TEXT_COLOR_CYAN + "login <USERNAME> <PASSWORD>" +  SET_TEXT_COLOR_WHITE + " - to login to an account \n" +
                SET_TEXT_COLOR_CYAN + "quit" + SET_TEXT_COLOR_WHITE + " - yeah you know what this does \n" +
                SET_TEXT_COLOR_CYAN + "help" + SET_TEXT_COLOR_WHITE + " - get some help");
    }

    public String quit() {
        return "Quitting...";
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            try {
                serverFacade.login(new UserData(params[0], params[1], null));
                return String.format("Welcome back, %s.", params[0]);
            } catch (ResponseException e) {
                throw new ResponseException(500, "Error: Incorrect Username or Password");
            }
        } else if (params.length > 2) {
            throw new ResponseException(400, "Error: Too many arguments given");
        } else {
            throw new ResponseException(400, "Error: Too few arguments given");
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            try {
                serverFacade.register(new UserData(params[0], params[1], params [2]));
                return "Welcome to Chess, a newfangled game.";
            } catch (ResponseException e) {
                throw new ResponseException(500, "Error: username already taken");
            }
        } else if (params.length > 3) {
            throw new ResponseException(400, "Error: Too many arguments given");
        } else {
            throw new ResponseException(400, "Error: Too few arguments given");
        }
    }

    public String clear() { // This is just for debugging
        try {
            serverFacade.clear();
        } catch (ResponseException e) {
            return "Not cleared";
        }
        return "Cleared";
    }

    public ServerFacade getServerFacade() {
        return serverFacade;
    }
}
