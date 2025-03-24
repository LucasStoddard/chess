package client;

import model.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private ServerFacade serverFacade;
    static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(8080);
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
            serverFacade.register(new UserData("a", "b", "c"));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }
}
