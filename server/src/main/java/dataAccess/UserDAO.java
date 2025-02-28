package dataAccess;

import model.UserData;

public interface UserDAO {
    UserData findUser(String username) throws DataAccessException;
    void addUser(UserData user);
    void login(UserData user) throws DataAccessException;
    void clear();
}
