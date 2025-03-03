package service;

import dataAccess.*;
import model.GameData;
import model.AuthData;
import java.util.HashSet;

public class GameService {
    AuthDAO authDAO;
    GameDAO gameDAO;

    public GameService(GameDAO gamedao, AuthDAO authdao) {
        authDAO = authdao;
        gameDAO = gamedao;
    }

    public HashSet<GameData> list(String authDataString) throws DataAccessException {
        authDAO.checkAuthData(authDataString);
        return gameDAO.getAllGames();
    }

    public GameData create(String authDataString, String gameName) throws DataAccessException {
        authDAO.checkAuthData(authDataString);
        int gameID = 1;
        while (gameDAO.ifGame(gameID)) {
            gameID++;
        }
        GameData newGame = new GameData(gameID, null, null, gameName, null);
        gameDAO.createGame(newGame);
        return newGame;
    }

    public void join(String authDataString, int gameID, String teamColor) throws DataAccessException {
        String username = authDAO.checkAuthData(authDataString);
        GameData tempGame = gameDAO.getGame(gameID);
        GameData newGame;
        if (teamColor.equals("WHITE")) {
            if (tempGame.whiteUsername() == null) {
                newGame = new GameData(tempGame.gameID(), username,
                        tempGame.blackUsername(), tempGame.gameName(), tempGame.game());
            } else {
                throw new DataAccessException("Error: already taken");
            }
        } else {
            if (tempGame.blackUsername() == null) {
                newGame = new GameData(tempGame.gameID(), tempGame.whiteUsername(),
                        username, tempGame.gameName(), tempGame.game());
            } else {
                throw new DataAccessException("Error: already taken");
            }
        }
        gameDAO.updateGame(newGame);
    }

    public void clear() {
        gameDAO.clear();
        authDAO.clear();
    }
}
