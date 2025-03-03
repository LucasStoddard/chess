package dataAccess;

import model.GameData;
import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {
    ArrayList<GameData> db;

    public MemoryGameDAO() {
        db = new ArrayList<>(100);
    }

    @Override
    public void createGame(GameData game) {
        db.add(game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData dbGame : db) {
            if (dbGame.gameID() == gameID) {
                return dbGame;
            }
        }
        throw new DataAccessException("Error: bad request");
    }

    @Override
    public boolean ifGame(int gameID) {
        for (GameData dbGame : db) {
            if (dbGame.gameID() == gameID) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ArrayList<GameData> getAllGames() {
        return db;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        GameData oldGame;
        try {
            oldGame = getGame(game.gameID());
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: bad request");
        }
        db.remove(oldGame);
        db.add(game);
    }

    @Override
    public void clear() {
        db = new ArrayList<>(100);
    }
}
