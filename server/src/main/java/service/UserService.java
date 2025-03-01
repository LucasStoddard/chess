package service;

import dataAccess.*;
import model.UserData;
import model.AuthData;

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

    public AuthData register(UserData registerRequest) {
        AuthData tempAuthData = new AuthData(registerRequest.username(), generateToken());
        userDAO.addUser(registerRequest);
        authDAO.addAuthData(tempAuthData);
        return tempAuthData;
    } // this is where I am

    public AuthData login(UserData loginRequest) {}
    public void logout(UserData logoutRequest) {}

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }
}
