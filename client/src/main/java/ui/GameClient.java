package ui;

import model.UserData;
import server.ServerFacade;
import java.util.Arrays;
import model.ResponseException;

import static ui.EscapeSequences.*;

public class GameClient {
//    private ServerFacade serverFacade;
//
//    public GameClient(ServerFacade serverF) {
//            serverFacade = serverF;
//    }
//
//    public String eval(String input) {
//        try {
//            var tokens = input.toLowerCase().split(" ");
//            var cmd = (tokens.length > 0) ? tokens[0] : "help";
//            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
//            return switch (cmd) {
//                case "quit" -> quit();
//                default -> help();
//            };
//        } catch (ResponseException e) {
//            return e.getMessage();
//        }
//    }
//
//    public String help() { // It required a lot of testing but this should work
//        return (SET_TEXT_COLOR_CYAN + "register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_DARK_GREY + " - to create an account \n" +
//                SET_TEXT_COLOR_CYAN + "login <USERNAME> <PASSWORD>" +  SET_TEXT_COLOR_DARK_GREY + " - to login to an account \n" +
//                SET_TEXT_COLOR_CYAN + "quit" + SET_TEXT_COLOR_DARK_GREY + " - yeah you know what this does \n" +
//                SET_TEXT_COLOR_CYAN + "help" + SET_TEXT_COLOR_DARK_GREY + " - get some help \n");
//    }

}
