package dataAccess;

import model.GameData;
import java.util.ArrayList;

public interface GameDAO {
    void createGame(GameData game);
    GameData getGame(int gameID) throws DataAccessException;
    ArrayList<GameData> getAllGames();
    void updateGame(GameData game) throws DataAccessException ;
    void clear();
}
