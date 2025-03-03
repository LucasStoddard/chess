package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryAuthDAO implements AuthDAO {
    ArrayList<AuthData> db;

    public MemoryAuthDAO() {
        db = new ArrayList<>(100);
    }

    @Override
    public void addAuthData(AuthData authData) {
        db.add(authData);
    }

    @Override
    public void findAuthData(AuthData authData) throws DataAccessException {
        if (!db.contains(authData)) {
            throw new DataAccessException("Error: unauthorized");
        }
    }

    @Override
    public void checkAuthData(String authData) throws DataAccessException {
        for (AuthData authUser : db) {
            if (Objects.equals(authUser.authToken(), authData)) {
                return;
            }
        }
        throw new DataAccessException("Error: unauthorized");
    }

    @Override
    public void deleteAuthData(AuthData authData) throws DataAccessException {
        if (!db.contains(authData)) {
            throw new DataAccessException("Error: unauthorized");
        } else {
            db.remove(authData);
        }
    }

    @Override
    public void clear() {
        db = new ArrayList<>(100);
    }
}
