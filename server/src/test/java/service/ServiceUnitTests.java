package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServiceUnitTests {
    @Test
    public void testRegisterPositive() throws DataAccessException {
        UserDAO user = new MemoryUserDAO();
        AuthDAO auth = new MemoryAuthDAO();
        UserService userS = new UserService(user, auth);
        UserData registerRequest = new UserData("newUser", "password123", "abc@123.org");
        AuthData authData = userS.register(registerRequest);
        Assertions.assertNotNull(authData);
        Assertions.assertEquals("newUser", authData.username());
    }

    @Test
    public void testRegisterNegative() throws DataAccessException {
        UserDAO user = new MemoryUserDAO();
        AuthDAO auth = new MemoryAuthDAO();
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
    public void testLoginPositive() throws DataAccessException {
        UserDAO user = new MemoryUserDAO();
        AuthDAO auth = new MemoryAuthDAO();
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
    public void testLoginNegative() throws DataAccessException {
        UserDAO user = new MemoryUserDAO();
        AuthDAO auth = new MemoryAuthDAO();
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
    public void testLogoutNegative() throws DataAccessException {
        UserDAO user = new MemoryUserDAO();
        AuthDAO auth = new MemoryAuthDAO();
        UserService userS = new UserService(user, auth);
        try {
            userS.logout("goobledeegook");
            Assertions.fail("Expected DataAccessException not thrown");
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test
    public void testLogoutPositive() throws DataAccessException {
        UserDAO user = new MemoryUserDAO();
        AuthDAO auth = new MemoryAuthDAO();
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
    public void testClearUserService() throws DataAccessException {
        UserDAO user = new MemoryUserDAO();
        AuthDAO auth = new MemoryAuthDAO();
        UserService userS = new UserService(user, auth);
        userS.clear();
    }

    @Test
    public void testClearGameService() throws DataAccessException {
        GameDAO game = new MemoryGameDAO();
        AuthDAO auth = new MemoryAuthDAO();
        GameService gameS = new GameService(game, auth);
        gameS.clear();
    }

    @Test
    public void testListPositive() throws DataAccessException {
        GameDAO game = new MemoryGameDAO();
        AuthDAO auth = new MemoryAuthDAO();
        UserDAO user = new MemoryUserDAO();
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
    public void testListNegative() throws DataAccessException {
        GameDAO game = new MemoryGameDAO();
        AuthDAO auth = new MemoryAuthDAO();
        GameService gameS = new GameService(game, auth);

        try {
            gameS.list("garbledeegook");
            Assertions.fail("Expected DataAccessException thrown");
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test
    public void testCreatePositive() throws DataAccessException {
        GameDAO game = new MemoryGameDAO();
        AuthDAO auth = new MemoryAuthDAO();
        UserDAO user = new MemoryUserDAO();
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
    public void testCreateNegative() throws DataAccessException {
        GameDAO game = new MemoryGameDAO();
        AuthDAO auth = new MemoryAuthDAO();
        UserDAO user = new MemoryUserDAO();
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
    public void testJoinPositive() throws DataAccessException {
        GameDAO game = new MemoryGameDAO();
        AuthDAO auth = new MemoryAuthDAO();
        UserDAO user = new MemoryUserDAO();
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
    public void testJoinNegative() throws DataAccessException {
        GameDAO game = new MemoryGameDAO();
        AuthDAO auth = new MemoryAuthDAO();
        UserDAO user = new MemoryUserDAO();
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









