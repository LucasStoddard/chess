package service;

import dataAccess.*;
import model.GameData;
import model.AuthData;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class GameService {
    AuthDAO authDAO;
    GameDAO gameDAO;

    public GameService(GameDAO gamedao, AuthDAO authdao) {
        authDAO = authdao;
        gameDAO = gamedao;
    }

    public ArrayList<GameData> list(AuthData authData) throws DataAccessException {
        authDAO.checkAuthData(authData.authToken());
        return gameDAO.getAllGames();
    }

    public GameData create(AuthData authData, String gameName) throws DataAccessException {
        authDAO.findAuthData(authData);
        int gameID = 1;
        while (gameDAO.ifGame(gameID)) {
            gameID++;
        }
        GameData newGame = new GameData(gameID, null, null, gameName, null);
        gameDAO.createGame(newGame);
        return newGame;
    }

    public void join(AuthData authData, int gameID, boolean isWhite) throws DataAccessException {
        authDAO.findAuthData(authData);
        GameData tempGame = gameDAO.getGame(gameID);
        GameData newGame;
        if (isWhite) {
            if (tempGame.whiteUsername() == null) {
                newGame = new GameData(tempGame.gameID(), authData.username(),
                        tempGame.blackUsername(), tempGame.gameName(), tempGame.game());
            } else {
                throw new DataAccessException("Error: already taken");
            }
        } else {
            if (tempGame.blackUsername() == null) {
                newGame = new GameData(tempGame.gameID(), tempGame.whiteUsername(),
                        authData.username(), tempGame.gameName(), tempGame.game());
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
