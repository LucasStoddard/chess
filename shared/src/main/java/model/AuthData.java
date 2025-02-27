package model;

import java.util.UUID;

public record AuthData(String username, String authToken) {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
