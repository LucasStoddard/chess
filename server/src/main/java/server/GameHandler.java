package server;

import com.google.gson.Gson;
import dataAccess.*;
import model.*;
import service.*;
import spark.Request;
import spark.Response;
import java.util.ArrayList;

public class GameHandler { // This is where (de)serialization happens
    GameService gameService;

    public GameHandler(GameService gameservice) {
        gameService = gameservice;
    }

    public Object list(Request req, Response resp) throws DataAccessException {
        String authDataString = req.headers("authorization");
        if (authDataString == null) {
            resp.status(500);
            resp.body("{ \"message\": \"Error: bad request\" }");
            throw new DataAccessException("Error: bad request");
        }
        try {
            ArrayList<GameData> games = gameService.list(authDataString);
            resp.status(200);
            return "{ \"games\": %s}".formatted(new Gson().toJson(games));
        } catch (DataAccessException e) {
            resp.status(401);
            resp.body("{ \"message\": \"Error: unauthorized\" }");
            throw new DataAccessException("Error: unauthorized");
        }
    }

    public Object create(Request req, Response resp) throws DataAccessException {
        String authDataString = req.headers("authorization");
        record gameNameRequest(String gameName) {}
        gameNameRequest gameName = new Gson().fromJson(req.body(), gameNameRequest.class);
        if (authDataString == null || gameName.gameName() == null) {
            resp.status(400);
            resp.body("{ \"message\": \"Error: bad request\" }");
            throw new DataAccessException("Error: bad request");
        }
        try {
            GameData newGame = gameService.create(authDataString, gameName.gameName());
            resp.status(200);
            return "{ \"gameID\": %s}".formatted(new Gson().toJson(newGame.gameID()));
        } catch (DataAccessException e) {
            resp.status(401);
            resp.body("{ \"message\": \"Error: unauthorized\" }");
            throw new DataAccessException("Error: unauthorized");
        }
    }

    public Object join(Request req, Response resp) throws DataAccessException {
        String authDataString = req.headers("authorization");
        if (authDataString == null) {
            resp.status(500);
            resp.body("{ \"message\": \"Error: bad request\" }");
            throw new DataAccessException("Error: bad request");
        }
        record gameJoinRecord(String playerColor, int gameID) {};
        gameJoinRecord gameJoin = new Gson().fromJson(req.body(), gameJoinRecord.class);
        try {
            gameService.join(authDataString, gameJoin.gameID(), gameJoin.playerColor);
            resp.status(200);
            return "{}";
        } catch (DataAccessException e) {
            resp.status(403);
            resp.body("{ \"message\": \"Error: already taken\" }");
            throw new DataAccessException(e.getMessage());
        }
    }
}
