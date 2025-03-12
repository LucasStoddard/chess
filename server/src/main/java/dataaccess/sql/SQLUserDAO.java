package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Objects;

public class SQLUserDAO implements UserDAO {
    private Connection conn;

    public SQLUserDAO(Connection connection) throws DataAccessException {
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
    public UserData findUser(String username) throws DataAccessException {
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        try (var preparedStatement = conn.prepareStatement("SELECT username, password, email FROM account_table")) {
            try (var rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    if (Objects.equals(username, rs.getString("username"))) {
                        return new UserData(username, rs.getString("password"), rs.getString("email"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("findUser error");
        }
        throw new DataAccessException("Error: user not found");
    }

    @Override
    public void login(UserData user) throws DataAccessException {
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        UserData dbUser;
        try {
            dbUser = findUser(user.username());
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: user not found");
        }
        if (!BCrypt.checkpw(user.password(), dbUser.password())) {
            throw new DataAccessException("Error: incorrect password");
        }
    }

    @Override
    public void addUser(UserData user) {
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        if (user.username().matches("[a-zA-Z]+")) {
            var statement = "INSERT INTO account_table (username, password, email) VALUES (?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
                preparedStatement.setString(3, user.email());

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("addUser error");
            }
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
            System.out.println("userClear error");
        }
    }
}
