package server;

import dataaccess.DatabaseManager;
import dataaccess.memory.*;
import dataaccess.sql.*;
import spark.*;
import dataaccess.*;
import service.*;

import java.sql.SQLException;

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
        try (var conn = DatabaseManager.getConnection()) {
            user = new SQLUserDAO(conn);
            auth = new SQLAuthDAO(conn);
            game = new SQLGameDAO(conn);
        } catch (DataAccessException | SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Server failed to set up");
        }
        userS = new UserService(user, auth);
        gameS = new GameService(game, auth);
        userH = new UserHandler(userS);
        gameH = new GameHandler(gameS);
    }

    // TODO: We are now passing most tests, there seems to be problems within the code (mainly for game creation but also a little with auth)
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here

        Spark.delete("/db", this::clear);
        Spark.post("/user", userH::register);
        Spark.post("/session", userH::login);
        Spark.delete("/session", userH::logout);
        Spark.get("/game", gameH::list);
        Spark.post("/game", gameH::create);
        Spark.put("/game", gameH::join);

        // This line initializes the server and can be removed once you have a functioning endpoint
        // Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public Object clear(Request req, Response resp) throws DataAccessException {
        userS.clear();
        gameS.clear();
        resp.status(200);
        return "{}";
    }
}
