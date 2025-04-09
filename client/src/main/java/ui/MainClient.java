package ui;

import model.*;
import server.ServerFacade;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Map;

import static ui.EscapeSequences.*;

public class MainClient {
    private final ServerFacade serverFacade;
    private Map<Integer, Integer> fakeToRealGameID = new HashMap<>();
    private GameUI gameui;
    private WebSocketFacade wsFacade;

    public MainClient(ServerFacade serverF, WebSocketFacade webSocketFacade, GameUI gameUI) {
        serverFacade = serverF;
        gameui = gameUI;
        wsFacade = webSocketFacade;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String help() { // It required a lot of testing but this should work
        return (SET_TEXT_COLOR_CYAN + "logout" + SET_TEXT_COLOR_WHITE + " - to log out \n" +
                SET_TEXT_COLOR_CYAN + "create <NAME>" +  SET_TEXT_COLOR_WHITE + " - to create a game (this does not join you to the game)\n" +
                SET_TEXT_COLOR_CYAN + "list" + SET_TEXT_COLOR_WHITE + " - to list all the games \n" +
                SET_TEXT_COLOR_CYAN + "join <ID> <WHITE/BLACK>" + SET_TEXT_COLOR_WHITE + " - to join a game by ID on WHITE or BLACK \n" +
                SET_TEXT_COLOR_CYAN + "observe <ID>" + SET_TEXT_COLOR_WHITE + " - to watch a game by ID \n" +
                SET_TEXT_COLOR_CYAN + "help" + SET_TEXT_COLOR_WHITE + " - get some help");
    }

    public String logout() throws ResponseException {
        try {
            serverFacade.logout(serverFacade.getAuth());
            return "Successfully logged out";
        } catch (ResponseException e) {
            throw new ResponseException(500, "Error: You are not authorized to do that");
        }
    }

    public String create(String... params) throws ResponseException { // if errors are here it may be authToken handling
        if (params.length == 1) {
            try {
                GameData game = serverFacade.create(serverFacade.getAuth(), params[0]);
                return "Game successfully created";
            } catch (ResponseException e) {
                throw new ResponseException(500, "Error: You are not authorized to do that");
            }
        } else if (params.length > 1) {
            throw new ResponseException(400, "Error: Too many arguments given");
        } else {
            throw new ResponseException(400, "Error: Too few arguments given");
        }
    }

    public String listPlayerHelper(String playerName) {
        if (playerName == null) {
            return "<empty>";
        } else {
            return playerName;
        }
    }

    public String list() throws ResponseException { // if errors are here it may be authToken handling
        try {
            HashSet<GameData> allGames = serverFacade.list(serverFacade.getAuth());
            String allGamesPrintable = "";
            int i = 1;
            if (fakeToRealGameID != null) {
                fakeToRealGameID.clear();
            }
            if (allGames.isEmpty()) {
                return "No games are available at the moment \n";
            }
            for (GameData game : allGames) {
                fakeToRealGameID.put(i, game.gameID());
                allGamesPrintable += (i + " " + game.gameName());
                allGamesPrintable += (" White Player: " + listPlayerHelper(game.whiteUsername()));
                allGamesPrintable += (" Black Player: " + listPlayerHelper(game.blackUsername()) + " \n");
                i++;
            }
            return allGamesPrintable;
        } catch (ResponseException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public Integer joinFilter(String gameID) throws ResponseException {
        Integer gameIdInt;
        try {
            gameIdInt = Integer.parseInt(gameID);
        } catch (Exception e) {
            throw new ResponseException(500, "Error: The game id must be a number");
        }
        if (fakeToRealGameID.containsKey(gameIdInt)) {
            return gameIdInt;
        } else {
            throw new ResponseException(500, "Error: A game of that ID does not exist");
        }
    }

    public String join(String... params) throws ResponseException {
        if (params.length == 2) {
            try {
                String team = params[1].toUpperCase();
                int newGameID = fakeToRealGameID.get(joinFilter(params[0]));
                serverFacade.join(serverFacade.getAuth(), newGameID, team);
                wsFacade.connect(serverFacade.getAuth(), newGameID, team);
                gameui.updateGameUI(serverFacade.getAuth(), newGameID);
                if (team.contains("WHITE")) {
                    gameui.updateTeam(false);
                    return "Joining game as white...";
                } else {
                    gameui.updateTeam(true);
                    return "Joining game as black...";
                }
            } catch (ResponseException e) {
                if (e.getMessage().contains("400")) {
                    throw new ResponseException(500, "Error: Invalid team color");
                } else if (e.getMessage().contains("401") | e.getMessage().contains("403")) {
                    throw new ResponseException(500, "Error: That team is already taken");
                } else if (e.getMessage().contains("game id")) {
                    throw new ResponseException(500, e.getMessage());
                } else {
                    throw new ResponseException(500, "Error: A game of that ID does not exist");
                }
            }
        } else if (params.length > 2) {
            throw new ResponseException(400, "Error: Too many arguments given");
        } else {
            throw new ResponseException(400, "Error: Too few arguments given");
        }
    }

    public String observe(String... params) throws ResponseException {
        if (params.length == 1) {
            try {
                joinFilter(params[0]);
                gameui.updateTeam(false);
                gameui.updateGameUI(serverFacade.getAuth(), fakeToRealGameID.get(joinFilter(params[0])));
                wsFacade.connect(serverFacade.getAuth(), fakeToRealGameID.get(joinFilter(params[0])), "observer");
                return "Joining game as an observer...";
            } catch (Exception e) {
                throw new ResponseException(400, e.getMessage());
            }
        } else if (params.length > 2) {
            throw new ResponseException(400, "Error: Too many arguments given");
        } else {
            throw new ResponseException(400, "Error: Too few arguments given");
        }
    }
}
