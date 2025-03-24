package client;

import model.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import model.*;


public class ServerFacadeTests {

    private static Server server;
    private ServerFacade serverFacade;
    static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    void setup() throws ResponseException {
        serverFacade = new ServerFacade("http://localhost:" + port);
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerPositive() throws ResponseException {
        try {
            AuthData auth = serverFacade.register(new UserData("a", "b", "c"));
            Assertions.assertNotNull(auth);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void registerNegative() throws ResponseException {
        try {
            serverFacade.register(new UserData("a", "b", "c"));
            serverFacade.register(new UserData("a", "b", "c"));
            Assertions.fail("Expected exception not thrown");
        } catch (Exception e) {
            Assertions.assertTrue((e.getMessage().contains("403")));
        }
    }

    @Test
    public void loginPositive() throws ResponseException {
        try {
            serverFacade.register(new UserData("a", "b", "c"));
            AuthData auth = serverFacade.login(new UserData("a", "b", null));
            Assertions.assertNotNull(auth);
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown");
        }
    }

    @Test
    public void loginNegative() throws ResponseException {
        try {
            serverFacade.register(new UserData("a", "b", "c"));
            AuthData auth = serverFacade.login(new UserData("a", "incorrectpassword", null));
            Assertions.fail("Expected exception not thrown");
        } catch (Exception e) {
            Assertions.assertTrue((e.getMessage().contains("401")));
        }
    }

    @Test
    public void logoutPositive() throws ResponseException {
        try {
            serverFacade.register(new UserData("a", "b", "c"));
            AuthData auth = serverFacade.login(new UserData("a", "b", null));
            serverFacade.logout(auth.authToken());
            Assertions.assertNotNull(auth);
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown");
        }
    }

    @Test
    public void logoutNegative() throws ResponseException {
        try {
            serverFacade.logout("wrongAuthToken");
            Assertions.fail("Expected exception not thrown");
        } catch (Exception e) {
            Assertions.assertTrue((e.getMessage().contains("401")));
        }
    }

    @Test
    public void listPositive() throws ResponseException {
        try {
            serverFacade.register(new UserData("a", "b", "c"));
            AuthData auth = serverFacade.login(new UserData("a", "b", null));
            Object games = serverFacade.list(auth.authToken());
            Assertions.assertNotNull(games);
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown");
        }
    }

    @Test
    public void listNegative() throws ResponseException {
        try {
            serverFacade.list("wrongAuthToken");
            Assertions.fail("Expected exception not thrown");
        } catch (Exception e) {
            Assertions.assertTrue((e.getMessage().contains("401")));
        }
    }

    @Test
    public void createPositive() throws ResponseException {
        try {
            serverFacade.register(new UserData("user", "pass", "c"));
            AuthData auth = serverFacade.login(new UserData("user", "pass", null));
            Object games = serverFacade.create(auth.authToken(), "newGame");
            Assertions.assertNotNull(games);
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown");
        }
    }

    @Test
    public void createNegative() throws ResponseException {
        try {
            serverFacade.register(new UserData("user", "pass", "c"));
            AuthData auth = serverFacade.login(new UserData("user", "pass", null));
            serverFacade.create("invalidAuthToken", "newGame");
            Assertions.fail("Expected exception not thrown");
        } catch (Exception e) {
            Assertions.assertTrue((e.getMessage().contains("401")));
        }
    }

    @Test
    public void joinPositive() throws ResponseException {
        try {
            serverFacade.register(new UserData("user", "pass", "c"));
            AuthData auth = serverFacade.login(new UserData("user", "pass", null));
            GameData game = serverFacade.create(auth.authToken(), "newGame");
            serverFacade.join(auth.authToken(), game.gameID(), "WHITE");
            Assertions.assertNotNull(game);
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown");
        }
    }

    @Test
    public void joinNegative() throws ResponseException {
        try {
            serverFacade.register(new UserData("user", "pass", "c"));
            AuthData auth = serverFacade.login(new UserData("user", "pass", null));
            GameData game = serverFacade.create(auth.authToken(), "newGame");
            serverFacade.join(auth.authToken(), game.gameID(), "WHITE");
            serverFacade.join(auth.authToken(), game.gameID(), "WHITE");
            Assertions.fail("Expected exception not thrown");
        } catch (Exception e) {
            Assertions.assertTrue((e.getMessage().contains("403")));
        }
    }

}
