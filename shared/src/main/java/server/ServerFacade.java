package server;

import model.AuthData;
import model.GameData;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Map;

import com.google.gson.Gson;
import model.UserData;
import model.ResponseException;

public class ServerFacade {
    private String url = "http://localhost:8080";

    public ServerFacade() {
    }

    public ServerFacade(String newUrl) {
        url = newUrl;
    }

    public AuthData register(UserData registerRequest) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, registerRequest, null, AuthData.class);
    }

    public AuthData login(UserData loginRequest) throws ResponseException {
        var path = "/session";
        var body = Map.of("username", loginRequest.username(), "password", loginRequest.password());
        return this.makeRequest("POST", path, body, null, AuthData.class);
    }

    public void logout(String logoutRequest) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, logoutRequest, null);
    }

    public HashSet<GameData> list(String authDataString) throws ResponseException {
        var path = "/game";
        record gameListResponse(HashSet<GameData> games) {
        }
        var response = this.makeRequest("GET", path, null, authDataString, gameListResponse.class);
        return response.games();
    }

    public GameData create(String authDataString, String gameName) throws ResponseException {
        var path = "/game";
        var body = Map.of("gameName", gameName);
        return this.makeRequest("POST", path, body, authDataString, GameData.class);
    }

    public void join(String authDataString, int gameID, String teamColor) throws ResponseException {
        var path = "/game";
        var body = Map.of("playerColor", teamColor, "gameID", gameID);
        this.makeRequest("PUT", path, body, authDataString, null);
    }

    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, String authorizationToken, Class<T> responseClass) throws ResponseException {
        try {
            URL requestURL = (new URI(url + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) requestURL.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (authorizationToken != null) {
                http.addRequestProperty("authorization", authorizationToken);
            }

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
