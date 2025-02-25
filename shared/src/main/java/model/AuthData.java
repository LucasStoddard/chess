package model;

import java.util.UUID;

public class AuthData {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
