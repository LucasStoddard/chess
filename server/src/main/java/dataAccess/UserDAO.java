package dataAccess;

import model.UserData;

public interface UserDAO {
    UserData findUser(String username) throws DataAccessException;
    void addUser(UserData user) throws DataAccessException;
    // login function?
    void clear();
}
