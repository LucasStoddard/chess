import model.AuthData;
import model.GameData;
import java.util.HashSet;
import model.UserData;

public class ServerFacade {
    String url = "http://localhost:8080";
    String authToken;

    public ServerFacade() {
    }

    public ServerFacade(String newUrl) {
        url = newUrl;
    }

    public boolean register(String username, String password, String email) {

    }

    public boolean login(String username, String password) {

    }

    public boolean logout() {

    }

    public HashSet<GameData> list() {

    }

    public boolean create(String gameName) {

    }

    public boolean join(int gameID, String teamColor) {

    }
}
