package server;

import com.google.gson.Gson;
import dataAccess.*;
import model.*;
import service.*;
import spark.Request;
import spark.Response;

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
            return "{ \"message\": \"Error: bad request\" }";
            //throw new DataAccessException("Error: bad request");
        }
        try {
            authData = userService.register(userData);
            resp.status(200);
            return new Gson().toJson(authData);
        } catch (DataAccessException e) {
            resp.status(403);
            return "{ \"message\": \"Error: already taken\" }";
        }
    }

    public Object login(Request req, Response resp) throws DataAccessException {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        AuthData authData;
        if (userData.username() == null || userData.password() == null) {
            resp.status(500);
            resp.body("{ \"message\": \"Error: bad request\" }");
            throw new DataAccessException("Error: bad request");
        }
        try {
            authData = userService.login(userData);
            resp.status(200);
            return new Gson().toJson(authData);
        } catch (DataAccessException e) {
            resp.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
            //throw new DataAccessException("Error: unauthorized");
        }
    }

    public Object logout(Request req, Response resp) throws DataAccessException {
        String authDataString = req.headers("authorization");
        if (authDataString == null) {
            resp.status(500);
            return "{ \"message\": \"Error: bad request\" }";
            //throw new DataAccessException("Error: bad request");
        }
        try {
            userService.logout(authDataString);
            resp.status(200);
            return "{}";
        } catch (DataAccessException e) {
            resp.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
    }
}
