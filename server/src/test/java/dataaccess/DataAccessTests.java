package dataaccess;

import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import org.junit.jupiter.api.*;
import model.*;
import service.*;
import java.sql.*;

import java.sql.SQLException;

public class DataAccessTests {
    GameDAO game;
    AuthDAO auth;
    UserDAO user;

    @BeforeEach
    public void setup() throws DataAccessException{
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            System.out.println("Database failed to be created");
        }
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
    public void testAuthInitializeNegativeSQL() {
        Connection falseConnection = null;
        try {
            AuthDAO falseAuth = new SQLAuthDAO(falseConnection);
            Assertions.fail("Expected Exception not thrown");
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().contains("Cannot invoke"), "Message should contain 'Cannot invoke'");
        }
    }

    @Test
    public void testGameInitializeNegativeSQL() {
        Connection falseConnection = null;
        try {
            GameDAO falseGame = new SQLGameDAO(falseConnection);
            Assertions.fail("Expected Exception not thrown");
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().contains("Cannot invoke"), "Message should contain 'Cannot invoke'");
        }
    }

    @Test
    public void testUserInitializeNegativeSQL() {
        Connection falseConnection = null;
        try {
            UserDAO falseUser = new SQLUserDAO(falseConnection);
            Assertions.fail("Expected Exception not thrown");
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().contains("Cannot invoke"), "Message should contain 'Cannot invoke'");
        }
    }

    @Test
    public void testRegisterPositiveSQL() throws DataAccessException {
        UserService userS = new UserService(user, auth);
        UserData registerRequest = new UserData("newUser", "password123", "abc@123.org");
        AuthData authData = userS.register(registerRequest);
        Assertions.assertNotNull(authData);
        Assertions.assertEquals("newUser", authData.username());
    }

    @Test
    public void testRegisterNegativeSQL() throws DataAccessException {
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
    public void testClearUserDaoSQL() throws DataAccessException {
        try {
            user.clear();
        } catch (DataAccessException e) {
            Assertions.fail("Unexpected DataAccessException thrown");
        }
    }

    @Test
    public void testClearAuthDaoSQL() throws DataAccessException {
        try {
            auth.clear();
        } catch (DataAccessException e) {
            Assertions.fail("Unexpected DataAccessException thrown");
        }
    }

    @Test
    public void testClearGameDaoSQL() throws DataAccessException {
        try {
            game.clear();
        } catch (DataAccessException e) {
            Assertions.fail("Unexpected DataAccessException thrown");
        }
    }

    @Test
    public void testClearUserServiceSQL() throws DataAccessException {
        UserService userS = new UserService(user, auth);
        try {
            userS.clear();
        } catch (DataAccessException e) {
            Assertions.fail("Unexpected DataAccessException thrown");
        }
    }

    @Test
    public void testClearGameServiceSQL() throws DataAccessException {
        GameService gameS = new GameService(game, auth);
        try {
            gameS.clear();
        } catch (DataAccessException e) {
            Assertions.fail("Unexpected DataAccessException thrown");
        }
    }

    @Test
    public void testListPositiveSQL() throws DataAccessException {
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









