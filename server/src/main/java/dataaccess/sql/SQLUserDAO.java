package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Objects;

public class SQLUserDAO extends SQLDAO implements UserDAO {
    public SQLUserDAO() throws DataAccessException {}

    @Override
    public UserData findUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT username, password, email FROM account_table")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        if (Objects.equals(username, rs.getString("username"))) {
                            return new UserData(username, rs.getString("password"), rs.getString("email"));
                        }
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error finding user");
        }
        throw new DataAccessException("Error: user not found");
    }

    @Override
    public void login(UserData user) throws DataAccessException {
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
    public void addUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            if (user.username().matches("[a-zA-Z]+")) {
                var statement = "INSERT INTO account_table (username, password, email) VALUES (?, ?, ?)";
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.setString(1, user.username());
                    preparedStatement.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
                    preparedStatement.setString(3, user.email());

                    preparedStatement.executeUpdate();
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error adding user");
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
            throw new DataAccessException("Error clearing tables");
        }
    }
}
