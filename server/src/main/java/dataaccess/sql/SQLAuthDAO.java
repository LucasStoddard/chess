package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.*;
import java.util.Objects;

public class SQLAuthDAO implements AuthDAO {
    private Connection conn;

    public SQLAuthDAO(Connection connection) throws DataAccessException {
        conn = connection;
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new DataAccessException("Error creating database");
        }
        var createAuthTable = """
                CREATE TABLE IF NOT EXISTS auth_table (
                    username VARCHAR(255) NOT NULL,
                    authToken TEXT NOT NULL
                )""";
        try (var createAccountTableStatement = conn.prepareStatement(createAuthTable)) {
            createAccountTableStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating auth table");
        }
    }

    @Override
    public void addAuthData(AuthData authData) {
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        var statement = "INSERT INTO auth_table (username, authToken) VALUES (?, ?)";
        try (var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, authData.username());
            preparedStatement.setString(2, authData.authToken());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("addAuthDao error");
        }
    }

    @Override
    public String checkAuthData(String authDataString) throws DataAccessException {
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        try (var preparedStatement = conn.prepareStatement("SELECT username, authToken FROM auth_table")) {
            try (var rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    if (Objects.equals(authDataString, rs.getString("authToken"))) {
                        return rs.getString("username");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("checkAuthData error");
        }
        throw new DataAccessException("Error: unauthorized");
    }

    @Override
    public void deleteAuthData(String authDataString) throws DataAccessException {
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        var statement = "DELETE FROM auth_table WHERE authToken = ?";
        try (var preparedStatement = conn.prepareStatement(statement)) {
            checkAuthData(authDataString);
            preparedStatement.setString(1, authDataString);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: unauthorized");
        }
    }

    @Override
    public void clear() {
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        var statement = "TRUNCATE TABLE auth_table";
        try (var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("authClear error");
        }
    }
}
