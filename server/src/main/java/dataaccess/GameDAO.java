package dataaccess;

import model.GameData;
import java.util.HashSet;

public interface GameDAO {
    void createGame(GameData game);
    GameData getGame(int gameID) throws DataAccessException;
    boolean ifGame(int gameID);
    HashSet<GameData> getAllGames();
    void updateGame(GameData game) throws DataAccessException ;
    void clear();
}
