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
        AuthData tempAuthData = new AuthData(registerRequest.username(), generateToken());
        try {
            userDAO.findUser(registerRequest.username());
        } catch (DataAccessException e) {
            userDAO.addUser(registerRequest);
            authDAO.addAuthData(tempAuthData);
            return tempAuthData;
        }
        throw new DataAccessException("Error: already taken");
    }

    public AuthData login(UserData loginRequest) throws DataAccessException {
        userDAO.login(loginRequest);
        AuthData tempAuthData = new AuthData(loginRequest.username(), generateToken());
        authDAO.addAuthData(tempAuthData);
        return tempAuthData;
    }

    public void logout(AuthData logoutRequest) throws DataAccessException {
        authDAO.deleteAuthData(logoutRequest);
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }
}
