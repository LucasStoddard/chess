package dataaccess;

import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import org.junit.jupiter.api.*;
import model.*;
import service.*;

import java.sql.SQLException;

public class DataAccessTests {
    @BeforeEach
    public void setup() throws DataAccessException{
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            System.out.println("Database failed to be created");
        }
        GameDAO game;
        AuthDAO auth;
        UserDAO user;
        try (var conn = DatabaseManager.getConnection()) {
            game = new SQLGameDAO(conn);
            auth = new SQLAuthDAO(conn);
            user = new SQLUserDAO(conn);
        } catch (DataAccessException | SQLException e) {
           throw new DataAccessException(e.getMessage());
        }
        game.clear();
        auth.clear();
        user.clear();

    }

    @Test
    public void testRegisterPositiveSQL() throws DataAccessException {
        UserDAO user;
        AuthDAO auth;
        try (var conn = DatabaseManager.getConnection()) {
            user = new SQLUserDAO(conn);
            auth = new SQLAuthDAO(conn);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        UserService userS = new UserService(user, auth);
        UserData registerRequest = new UserData("newUser", "password123", "abc@123.org");
        AuthData authData = userS.register(registerRequest);
        Assertions.assertNotNull(authData);
        Assertions.assertEquals("newUser", authData.username());
    }

    @Test
    public void testRegisterNegativeSQL() throws DataAccessException {
        UserDAO user;
        AuthDAO auth;
        try (var conn = DatabaseManager.getConnection()) {
            user = new SQLUserDAO(conn);
            auth = new SQLAuthDAO(conn);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        UserService userS = new UserService(user, auth);
        UserData registerRequest = new UserData("newUser", "password123", "abc@123.org");
        userS.register(registerRequest);
        try {
            AuthData authData = userS.register(registerRequest);
            Assertions.fail("Expected DataAccessException not thrown");
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: already taken", e.getMessage());
        }
    }

    @Test
    public void testLoginPositiveSQL() throws DataAccessException {
        UserDAO user;
        AuthDAO auth;
        try (var conn = DatabaseManager.getConnection()) {
            user = new SQLUserDAO(conn);
            auth = new SQLAuthDAO(conn);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        UserService userS = new UserService(user, auth);
        UserData loginRequest = new UserData("newUser", "password123", "abc@123.org");
        userS.register(loginRequest);
        try {
            userS.login(loginRequest);
        } catch (DataAccessException e) {
            Assertions.fail("Unexpected DataAccessException thrown");
        }
    }

    @Test
    public void testLoginNegativeSQL() throws DataAccessException {
        UserDAO user;
        AuthDAO auth;
        try (var conn = DatabaseManager.getConnection()) {
            user = new SQLUserDAO(conn);
            auth = new SQLAuthDAO(conn);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        UserService userS = new UserService(user, auth);
        UserData registerRequest = new UserData("newUser", "password123", "abc@123.org");
        userS.register(registerRequest);
        UserData badLoginRequest = new UserData("newUser", "123password", "abc@123.org");
        try {
            userS.login(badLoginRequest);
            Assertions.fail("Expected DataAccessException not thrown");
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: incorrect password", e.getMessage());
        }
    }

    @Test
    public void testLogoutNegativeSQL() throws DataAccessException {
        UserDAO user;
        AuthDAO auth;
        try (var conn = DatabaseManager.getConnection()) {
            user = new SQLUserDAO(conn);
            auth = new SQLAuthDAO(conn);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        UserService userS = new UserService(user, auth);
        try {
            userS.logout("goobledeegook");
            Assertions.fail("Expected DataAccessException not thrown");
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test
    public void testLogoutPositiveSQL() throws DataAccessException {
        UserDAO user;
        AuthDAO auth;
        try (var conn = DatabaseManager.getConnection()) {
            user = new SQLUserDAO(conn);
            auth = new SQLAuthDAO(conn);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        UserService userS = new UserService(user, auth);
        UserData registerRequest = new UserData("newUser", "password123", "abc@123.org");
        AuthData authData = userS.register(registerRequest);
        try {
            userS.logout(authData.authToken());
        } catch (DataAccessException e) {
            Assertions.fail("Unexpected DataAccessException thrown");
        }
    }

    @Test
    public void testClearUserServiceSQL() throws DataAccessException {
        UserDAO user;
        AuthDAO auth;
        try (var conn = DatabaseManager.getConnection()) {
            user = new SQLUserDAO(conn);
            auth = new SQLAuthDAO(conn);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        UserService userS = new UserService(user, auth);
        userS.clear();
    }

    @Test
    public void testClearGameServiceSQL() throws DataAccessException {
        GameDAO game;
        AuthDAO auth;
        try (var conn = DatabaseManager.getConnection()) {
            game = new SQLGameDAO(conn);
            auth = new SQLAuthDAO(conn);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        GameService gameS = new GameService(game, auth);
        gameS.clear();
    }

    @Test
    public void testListPositiveSQL() throws DataAccessException {
        GameDAO game;
        AuthDAO auth;
        UserDAO user;
        try (var conn = DatabaseManager.getConnection()) {
            game = new SQLGameDAO(conn);
            auth = new SQLAuthDAO(conn);
            user = new SQLUserDAO(conn);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        UserService userS = new UserService(user, auth);
        GameService gameS = new GameService(game, auth);
        UserData registerRequest = new UserData("newUser", "password123", "abc@123.org");
        AuthData authData = userS.register(registerRequest);

        try {
            gameS.list(authData.authToken());
        } catch (DataAccessException e) {
            Assertions.fail("Unexpected DataAccessException thrown");
        }
    }

    @Test
    public void testListNegativeSQL() throws DataAccessException {
        GameDAO game;
        AuthDAO auth;
        try (var conn = DatabaseManager.getConnection()) {
            game = new SQLGameDAO(conn);
            auth = new SQLAuthDAO(conn);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        GameService gameS = new GameService(game, auth);

        try {
            gameS.list("garbledeegook");
            Assertions.fail("Expected DataAccessException thrown");
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test
    public void testCreatePositiveSQL() throws DataAccessException {
        GameDAO game;
        AuthDAO auth;
        UserDAO user;
        try (var conn = DatabaseManager.getConnection()) {
            game = new SQLGameDAO(conn);
            auth = new SQLAuthDAO(conn);
            user = new SQLUserDAO(conn);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        UserService userS = new UserService(user, auth);
        GameService gameS = new GameService(game, auth);
        UserData registerRequest = new UserData("newUser", "password123", "abc@123.org");
        AuthData authData = userS.register(registerRequest);

        try {
            gameS.create(authData.authToken(), "newGame");
        } catch (DataAccessException e) {
            Assertions.fail("Unexpected DataAccessException thrown");
        }
    }

    @Test
    public void testCreateNegativeSQL() throws DataAccessException {
        GameDAO game;
        AuthDAO auth;
        UserDAO user;
        try (var conn = DatabaseManager.getConnection()) {
            game = new SQLGameDAO(conn);
            auth = new SQLAuthDAO(conn);
            user = new SQLUserDAO(conn);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        UserService userS = new UserService(user, auth);
        GameService gameS = new GameService(game, auth);
        UserData registerRequest = new UserData("newUser", "password123", "abc@123.org");
        AuthData authData = userS.register(registerRequest);

        try {
            gameS.create("Invalid token", "newGame");
            Assertions.fail("Expected DataAccessException thrown");
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test
    public void testJoinPositiveSQL() throws DataAccessException {
        GameDAO game;
        AuthDAO auth;
        UserDAO user;
        try (var conn = DatabaseManager.getConnection()) {
            game = new SQLGameDAO(conn);
            auth = new SQLAuthDAO(conn);
            user = new SQLUserDAO(conn);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        UserService userS = new UserService(user, auth);
        GameService gameS = new GameService(game, auth);
        UserData registerRequest = new UserData("newUser", "password123", "abc@123.org");
        AuthData authData = userS.register(registerRequest);
        GameData gameData = gameS.create(authData.authToken(), "newGame");

        try {
            gameS.join(authData.authToken(), gameData.gameID(), "WHITE");
        } catch (DataAccessException e) {
            Assertions.fail("Unexpected DataAccessException thrown");
        }
    }

    @Test
    public void testJoinNegativeSQL() throws DataAccessException {
        GameDAO game;
        AuthDAO auth;
        UserDAO user;
        try (var conn = DatabaseManager.getConnection()) {
            game = new SQLGameDAO(conn);
            auth = new SQLAuthDAO(conn);
            user = new SQLUserDAO(conn);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        UserService userS = new UserService(user, auth);
        GameService gameS = new GameService(game, auth);
        UserData registerRequest = new UserData("newUser", "password123", "abc@123.org");
        AuthData authData = userS.register(registerRequest);
        GameData gameData = gameS.create(authData.authToken(), "newGame");
        gameS.join(authData.authToken(), gameData.gameID(), "WHITE");
        try {
            gameS.join(authData.authToken(), gameData.gameID(), "WHITE");
            Assertions.fail("Unexpected DataAccessException thrown");
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: already taken", e.getMessage());
        }
    }
}









