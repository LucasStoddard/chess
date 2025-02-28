package service;

import dataAccess.*;
import model.GameData;
import model.AuthData;
import java.util.ArrayList;

public class GameService {
    public ArrayList<GameData> list(AuthData authData) {}
    public GameData create(AuthData authData, int gameID) {}
    public void join(AuthData authData, int gameID, boolean isWhite) {}
    public void clear() {}
}
