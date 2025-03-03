package server;

import com.google.gson.Gson;
import dataAccess.*;
import model.*;
import service.*;
import spark.Request;
import spark.Response;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public class GameHandler { // This is where (de)serialization happens
    GameService gameService;

    public GameHandler(GameService gameservice) {
        gameService = gameservice;
    }

    public Object list(Request req, Response resp) throws DataAccessException {
        String authData = req.headers("authorization");
        if (authData == null) {
            resp.status(500);
            resp.body("{ \"message\": \"Error: bad request\" }");
            throw new DataAccessException("Error: bad request");
        }
        try {
            list(authData) // I AM HERE
        } catch (DataAccessException e) {

        }
    }

    public Object create(Request req, Response resp) throws DataAccessException {
        String authData = req.headers("authorization");
        if (authData == null) {
            resp.status(500);
            resp.body("{ \"message\": \"Error: bad request\" }");
            throw new DataAccessException("Error: bad request");
        }
    }

    public Object join(Request req, Response resp) throws DataAccessException {
        String authData = req.headers("authorization");
        if (authData == null) {
            resp.status(500);
            resp.body("{ \"message\": \"Error: bad request\" }");
            throw new DataAccessException("Error: bad request");
        }
    }
}
