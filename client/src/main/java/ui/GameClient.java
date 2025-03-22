package ui;

import model.UserData;
import server.ServerFacade;
import java.util.Arrays;
import model.ResponseException;

import static ui.EscapeSequences.*;

public class GameClient {
    private ServerFacade serverFacade;

    public LoginClient(String serverUrl) {
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

        public String help() { // It required a lot of testing but this should work
            return (SET_TEXT_COLOR_CYAN + "register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_DARK_GREY + " - to create an account \n" +
                    SET_TEXT_COLOR_CYAN + "login <USERNAME> <PASSWORD>" +  SET_TEXT_COLOR_DARK_GREY + " - to login to an account \n" +
                    SET_TEXT_COLOR_CYAN + "quit" + SET_TEXT_COLOR_DARK_GREY + " - yeah you know what this does \n" +
                    SET_TEXT_COLOR_DARK_GREY + "help" + SET_TEXT_COLOR_DARK_GREY + " - get some help \n");
        }

        public String quit() {
            return "quit";
        }

        public String login(String... params) throws ResponseException {
            if (params.length == 2) {
                try {
                    serverFacade.login(new UserData(params[0], params[1], null));
                    return String.format("Welcome back, %s.", params[0]);
                } catch (ResponseException e) {
                    throw new ResponseException(500, e.getMessage());
                }
            } else if (params.length > 2) {
                throw new ResponseException(400, "Too many arguments given");
            } else {
                throw new ResponseException(400, "Too few arguments given");
            }
        }

        public String register(String... params) throws ResponseException {
            if (params.length == 3) {
                try {
                    serverFacade.register(new UserData(params[0], params[1], params [2]));
                    return "Welcome to Chess, a newfangled game.";
                } catch (ResponseException e) {
                    throw new ResponseException(500, e.getMessage());
                }
            } else if (params.length > 3) {
                throw new ResponseException(400, "Too many arguments given");
            } else {
                throw new ResponseException(400, "Too few arguments given");
            }
        }
    }
    }
}
