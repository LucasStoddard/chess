package dataAccess;

import model.GameData;
import java.util.ArrayList;

public interface GameDAO {
    void createGame(GameData game);
    GameData getGame(int gameID);
    ArrayList<GameData> getAllGames();
    void updateGame(GameData game);
    void clear();
}
