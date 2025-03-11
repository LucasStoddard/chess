package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.*;
import java.util.Objects;

public class SQLAuthDAO extends SQLDAO implements AuthDAO {
    public SQLAuthDAO() throws DataAccessException {}

    @Override
    public void addAuthData(AuthData authData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE account_table SET authToken = ? WHERE username = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.setString(2, authData.username());

                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error adding user");
        }
    }

    @Override
    public String checkAuthData(String authDataString) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT username, authToken FROM account_table")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        if (Objects.equals(authDataString, rs.getString("authToken"))) {
                            return rs.getString("username");
                        }
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error finding user");
        }
        throw new DataAccessException("Error: unauthorized");
    }

    @Override
    public void deleteAuthData(String authDataString) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE account_table SET authToken = ? WHERE username = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setNull(1, Types.CLOB);
                preparedStatement.setString(2, checkAuthData(authDataString));

                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error: Unauthorized");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE TABLE account_table";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error clearing account table");
        }
    }
}
