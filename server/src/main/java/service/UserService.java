package service;

import dataaccess.*;
import model.UserData;
import model.AuthData;

import javax.xml.crypto.Data;
import java.util.UUID;

public class UserService { // This is where (de)serialization happens
    UserDAO userDAO;
    AuthDAO authDAO;

    public UserService(UserDAO userdao, AuthDAO authdao) {
        userDAO = userdao;
        authDAO = authdao;
    }

    public AuthData register(UserData registerRequest) throws DataAccessException {
        AuthData tempAuthData = new AuthData(UUID.randomUUID().toString(), registerRequest.username());
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
        AuthData tempAuthData = new AuthData(UUID.randomUUID().toString(), loginRequest.username());
        authDAO.addAuthData(tempAuthData);
        return tempAuthData;
    }

    public void logout(String logoutRequest) throws DataAccessException {
        authDAO.deleteAuthData(logoutRequest);
    }

    public void clear() throws DataAccessException {
        try {
            userDAO.clear();
            authDAO.clear();
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
