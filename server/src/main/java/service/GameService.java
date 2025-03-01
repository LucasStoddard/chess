package service;

import dataAccess.*;
import model.GameData;
import model.AuthData;
import java.util.ArrayList;

public class GameService {
    UserDAO userDAO;
    GameDAO gameDAO;

    public GameService(UserDAO userdao, GameDAO gamedao) {
        userDAO = userdao;
        gameDAO = gamedao;
    }

    public ArrayList<GameData> list(AuthData authData) {}
    public GameData create(AuthData authData, int gameID) {}
    public void join(AuthData authData, int gameID, boolean isWhite) {}
    public void clear() {}
}
