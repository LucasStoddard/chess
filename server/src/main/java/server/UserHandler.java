package server;

import com.google.gson.Gson;
import dataAccess.*;
import model.*;
import service.*;
import spark.Request;
import spark.Response;
import java.util.ArrayList;

public class UserHandler {
    UserService userService;

    public UserHandler(UserService userservice) {
        userService = userservice;
    }

    public Object register(Request req, Response resp) throws DataAccessException {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        AuthData authData;
        if (userData.username() == null || userData.password() == null) {
            resp.status(400);
            throw new DataAccessException("Error: bad request");
        }
        try {
            authData = userService.register(userData);
            resp.status(200);
            return new Gson().toJson(authData);
        } catch (DataAccessException e) { // I'm not sure about the 500 error
            resp.status(403);
            throw new DataAccessException(e.getMessage());
        }
    }

    public Object login(Request req, Response resp) throws DataAccessException {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        AuthData authData;
        if (userData.username() == null || userData.password() == null) {
            resp.status(500);
            throw new DataAccessException("Error: bad request");
        }
        try {
            authData = userService.login(userData);
            resp.status(200);
            return new Gson().toJson(authData);
        } catch (DataAccessException e) {
            resp.status(401);
            throw new DataAccessException("Error: unauthorized");
        }
    }

    public Object logout(Request req, Response resp) throws DataAccessException {
        AuthData authData = new Gson().fromJson(req.body(), AuthData.class);
        if (authData.username() == null || authData.authToken() == null) {
            resp.status(500);
            throw new DataAccessException("Error: bad request");
        }
        try {
            userService.logout(authData);
            resp.status(200);
            return new Gson().toJson(authData);
        } catch (DataAccessException e) {
            resp.status(401);
            throw new DataAccessException(e.getMessage());
        }
    }
}
