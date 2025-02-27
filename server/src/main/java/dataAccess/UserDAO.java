package dataAccess;

import model.UserData;

public interface UserDAO {
    UserData findUser(String username);
    void addUser(UserData user);
    void clear();
}
