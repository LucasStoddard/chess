package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.UserData;
import model.GameData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.HashSet;
import java.util.Objects;

public class SQLGameDAO extends SQLDAO implements GameDAO {
    public SQLGameDAO() throws DataAccessException {}

    @Override
    public void createGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            if (game.gameName().matches("[a-zA-Z]+")) {
                var statement = "INSERT INTO game_table (username, password, email) VALUES (?, ?, ?)";
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
    public GameData getGame(int gameID) throws DataAccessException {

    }

    @Override
    public boolean ifGame(int gameID) throws DataAccessException {

    }

    @Override
    public HashSet<GameData> getAllGames() throws DataAccessException {

    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
