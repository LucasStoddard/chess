package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import service.*;
import spark.Request;
import spark.Response;
import java.util.HashSet;

public class GameHandler { // This is where (de)serialization happens
    GameService gameService;

    public GameHandler(GameService gameservice) {
        gameService = gameservice;
    }

    public Object list(Request req, Response resp) throws DataAccessException {
        String authDataString = req.headers("authorization");
        if (authDataString == null) {
            resp.status(500);
            return "{ \"message\": \"Error: bad request\" }";
            // throw new DataAccessException("Error: bad request");
        }
        try {
            HashSet<GameData> games = gameService.list(authDataString);
            resp.status(200);
            return "{ \"games\": %s}".formatted(new Gson().toJson(games));
        } catch (DataAccessException e) {
            resp.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
            // throw new DataAccessException("Error: unauthorized");
        }
    }

    public Object create(Request req, Response resp) throws DataAccessException {
        String authDataString = req.headers("authorization");
        record GameNameRequest(String gameName) {}
        GameNameRequest gameName = new Gson().fromJson(req.body(), GameNameRequest.class);
        if (authDataString == null || gameName == null || gameName.gameName() == null) {
            resp.status(400);
            return "{ \"message\": \"Error: bad request\" }";
            // throw new DataAccessException("Error: bad request");
        }
        try {
            GameData newGame = gameService.create(authDataString, gameName.gameName());
            resp.status(200);
            return "{ \"gameID\": %s}".formatted(new Gson().toJson(newGame.gameID()));
        } catch (DataAccessException e) {
            resp.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
            // throw new DataAccessException("Error: unauthorized");
        }
    }

    public Object join(Request req, Response resp) throws DataAccessException {
        String authDataString = req.headers("authorization");
        record GameJoinRecord(String playerColor, int gameID) {};
        GameJoinRecord gameJoin = new Gson().fromJson(req.body(), GameJoinRecord.class);
        if (authDataString == null || gameJoin == null) {
            resp.status(400);
            return "{ \"message\": \"Error: bad request\" }";
            // throw new DataAccessException("Error: bad request");
        }
        if (gameJoin.playerColor() == null) {
            resp.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        } else if (!gameJoin.playerColor().equals("WHITE") && !gameJoin.playerColor().equals("BLACK")) {
            resp.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        }
        try {
            gameService.join(authDataString, gameJoin.gameID(), gameJoin.playerColor);
            resp.status(200);
            return "{}";
        } catch (DataAccessException e) {
            if (e.getMessage().contains("invalid game")) {
                resp.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            } else if (e.getMessage().contains("unauthorized")) {
                resp.status(401);
                return "{ \"message\": \"Error: already taken\" }";
            } else {
                resp.status(403);
                return "{ \"message\": \"Error: already taken\" }";
            }
        }
    }
}
