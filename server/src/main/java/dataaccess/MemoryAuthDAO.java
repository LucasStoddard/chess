package dataaccess;

import model.AuthData;

import java.util.HashSet;
import java.util.Objects;

public class MemoryAuthDAO implements AuthDAO {
    HashSet<AuthData> db;

    public MemoryAuthDAO() {
        db = new HashSet<>(100);
    }

    @Override
    public void addAuthData(AuthData authData) {
        db.add(authData);
    }

    @Override
    public String checkAuthData(String authDataString) throws DataAccessException {
        for (AuthData authUser : db) {
            if (Objects.equals(authUser.authToken(), authDataString)) {
                return authUser.username();
            }
        }
        throw new DataAccessException("Error: unauthorized");
    }

    @Override
    public void deleteAuthData(String authDataString) throws DataAccessException {
        for (AuthData authUser : db) {
            if (Objects.equals(authUser.authToken(), authDataString)) {
                db.remove(authUser);
                return;
            }
        }
        throw new DataAccessException("Error: unauthorized");
    }

    @Override
    public void clear() {
        db = new HashSet<>(100);
    }
}
