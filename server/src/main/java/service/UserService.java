package service;

import dataAccess.*;
import model.UserData;
import model.AuthData;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.UUID;

public class UserService { // This is where (de)serialization happens
    UserDAO userDAO;
    AuthDAO authDAO;

    public UserService(UserDAO userdao, AuthDAO authdao) {
        userDAO = userdao;
        authDAO = authdao;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData register(UserData registerRequest) throws DataAccessException {
        try {
            userDAO.addUser(registerRequest);
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public AuthData login(UserData loginRequest) {}
    public void logout(UserData logoutRequest) {}

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }
}
