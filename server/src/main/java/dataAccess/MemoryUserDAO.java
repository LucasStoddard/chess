package dataAccess;

import model.UserData;
import java.util.ArrayList;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {
    ArrayList<UserData> db;

    public MemoryUserDAO() {
        db = new ArrayList<>(100);
    }

    @Override
    public UserData findUser(String username) throws DataAccessException {
        for (UserData dbUser : db) {
            if (Objects.equals(dbUser.username(), username)) {
                return dbUser;
            }
        }
        throw new DataAccessException("Error: user not found");
    }

    @Override
    public void login(UserData user) throws DataAccessException {
        UserData dbUser;
        try {
            dbUser = findUser(user.username());
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: user not found");
        }
        if (!Objects.equals(dbUser.password(), user.password())) {
            throw new DataAccessException("Error: incorrect password");
        }
    }

    @Override
    public void addUser(UserData user) {
        db.add(user);
    }

    @Override
    public void clear(){
        db = new ArrayList<>(100);
    }
}
