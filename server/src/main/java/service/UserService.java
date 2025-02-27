package service;

import dataAccess.*;
import model.UserData;
import model.AuthData;
import java.util.ArrayList;
import java.util.UUID;

public class UserService {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
    //public AuthData register(UserData registerRequest) {}
    //public AuthData login(UserData loginRequest) {}
    //public void logout(UserData logoutRequest) {}
    //public void clear() {}
}
