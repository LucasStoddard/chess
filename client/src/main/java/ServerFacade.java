import model.AuthData;
import model.GameData;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import com.google.gson.Gson;
import model.UserData;
import ui.ResponseException;

public class ServerFacade {
    private String url = "http://localhost:8080";

    public ServerFacade() {
    }

    public ServerFacade(String newUrl) {
        url = newUrl;
    }

    public AuthData register(UserData registerRequest) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, registerRequest, AuthData.class);
    }

    public AuthData login(UserData loginRequest) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, loginRequest, AuthData.class);
    }

    public void logout(String logoutRequest) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, logoutRequest, AuthData.class);
    }

    public HashSet<GameData> list(String authDataString) throws ResponseException {
        var path = "/game";
        record gameListResponse(HashSet<GameData> allGames) {
        }
        var response = this.makeRequest("GET", path, authDataString, gameListResponse.class);
        return response.allGames();
    }

    public GameData create(String authDataString, String gameName) throws ResponseException {
        var path = "/game";
        this.makeRequest("POST", path, logoutRequest, AuthData.class);
    }

    public void join(String authDataString, int gameID, String teamColor) throws ResponseException {

    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL requestURL = (new URI(url + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) requestURL.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return (status / 100 == 2);
    }


}
