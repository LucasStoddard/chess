package dataaccess;

import model.GameData;
import java.util.HashSet;

public interface GameDAO {
    void createGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    boolean ifGame(int gameID) throws DataAccessException;
    HashSet<GameData> getAllGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException ;
    void clear() throws DataAccessException;
}
