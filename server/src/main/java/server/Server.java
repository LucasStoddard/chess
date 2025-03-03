package server;

import spark.*;
import com.google.gson.Gson;
import dataAccess.*;
import service.*;
import server.UserHandler;
import server.GameHandler;

public class Server {
    // JSON.stringify;
    UserDAO user;
    AuthDAO auth;
    GameDAO game;
    UserService userS;
    GameService gameS;
    UserHandler userH;
    GameHandler gameH;

    public Server() {
        user = new MemoryUserDAO();
        auth = new MemoryAuthDAO();
        game = new MemoryGameDAO();
        userS = new UserService(user, auth);
        gameS = new GameService(game, auth);
        userH = new UserHandler(userS);
        gameH = new GameHandler(gameS);
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web"); // TODO: UserHandler needs resp.body work
        // TODO: Many services assume authData will be received, unfortunately it will most often be just a string so it need to be switched with checkAuthData();

        // Register your endpoints and handle exceptions here

        Spark.delete("/db", this::clear);
        Spark.post("/user", userH::register);
        Spark.post("/session", userH::login);
        Spark.delete("/session", userH::logout);
//        Spark.get("/game", gameH::listGames);
//        Spark.post("/game", gameH::createGame);
//        Spark.put("/game", gameH::joinGame);

        // This line initializes the server and can be removed once you have a functioning endpoint
        // Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public Object clear(Request req, Response resp) {
        userS.clear();
        gameS.clear();
        resp.status(200);
        return "{}";
    }
}
