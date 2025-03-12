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
        var createAccountTable = """
                CREATE TABLE IF NOT EXISTS account_table (
                    username VARCHAR(255) NOT NULL,
                    password TEXT NOT NULL,
                    email VARCHAR(255) NOT NULL,
                    authToken TEXT
                )""";
        try (var createAccountTableStatement = conn.prepareStatement(createAccountTable)) {
            createAccountTableStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating account table");
        }
    }

    @Override
    public void addAuthData(AuthData authData) throws DataAccessException {
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        var statement = "UPDATE account_table SET authToken = ? WHERE username = ?";
        try (var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, authData.authToken());
            preparedStatement.setString(2, authData.username());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error adding user");
        }
    }

    @Override
    public String checkAuthData(String authDataString) throws DataAccessException {
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        try (var preparedStatement = conn.prepareStatement("SELECT username, authToken FROM account_table")) {
            try (var rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    if (Objects.equals(authDataString, rs.getString("authToken"))) {
                        return rs.getString("username");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding user");
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
        var statement = "UPDATE account_table SET authToken = ? WHERE username = ?";
        try (var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setNull(1, Types.CLOB);
            preparedStatement.setString(2, checkAuthData(authDataString));

            preparedStatement.executeUpdate();
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error: Unauthorized");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        var statement = "TRUNCATE TABLE account_table";
        try (var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing account table");
        }
    }
}
