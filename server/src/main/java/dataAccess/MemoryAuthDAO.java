package dataAccess;

import model.AuthData;
import java.util.ArrayList;

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
    public AuthData findAuthData(AuthData authData) throws DataAccessException {
        if (!db.contains(authData)) {
            throw new DataAccessException("Error: unauthorized");
        } else {
            return authData;
        }
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
