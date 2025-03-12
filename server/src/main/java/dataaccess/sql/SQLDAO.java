package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.*;


public abstract class SQLDAO {
    public SQLDAO() throws DataAccessException {
        setUpDatabase();
    }
    
    private void setUpDatabase() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new DataAccessException("Error creating database");
        }
        try (var conn = DatabaseManager.getConnection()) { // table initializations
            var createAccountTable = """
                    CREATE TABLE IF NOT EXISTS account_table (
                        username VARCHAR(255) NOT NULL,
                        password TEXT NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        authToken TEXT,
                    )""";
            var createGameTable = """
                    CREATE TABLE IF NOT EXISTS game_table (
                        gameID INT NOT NULL AUTO_INCREMENT,
                        whiteUsername VARCHAR(255),
                        blackUsername VARCHAR(255),
                        gameName VARCHAR(255) NOT NULL,
                        game TEXT,
                    )""";
            try (var createAccountTableStatement = conn.prepareStatement(createAccountTable)) {
                createAccountTableStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error creating account table");
            }
            try (var createGameTableStatement = conn.prepareStatement(createGameTable)) {
                createGameTableStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error creating game table");
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error setting up database");
        }
    }
}
