package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.*;
import java.util.HashSet;


public class SQLGameDAO extends SQLDAO implements GameDAO {
    public SQLGameDAO() throws DataAccessException {}

    @Override
    public void createGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            if (game.gameName().matches("[a-zA-Z]+")) {
                var statement = "INSERT INTO game_table (gameID, whiteUsername, blackUsername, gameName, ChessGame) VALUES (?, ?, ?, ?, ?)";
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.setInt(1, game.gameID());
                    if (game.whiteUsername() == null) {
                        preparedStatement.setNull(2, Types.VARCHAR);
                    } else {
                        preparedStatement.setString(2, game.whiteUsername());
                    }
                    if (game.blackUsername() == null) {
                        preparedStatement.setNull(3, Types.VARCHAR);
                    } else {
                        preparedStatement.setString(3, game.blackUsername());
                    }
                    preparedStatement.setString(4, game.gameName());
                    preparedStatement.setNull(5, Types.CLOB); // it was getting mad at Types.TEXT

                    preparedStatement.executeUpdate();
                }
            } else {
                throw new DataAccessException("Error: invalid name");
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error creating game");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, " +
                    "gameName, ChessGame FROM game_table")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        if (gameID == rs.getInt("gameID")) { // these getStrings should just return null if null is there
                            return new GameData(gameID, rs.getString("whiteUsername"), rs.getString("blackUsername"),
                                    rs.getString("gameName"), new Gson().fromJson(rs.getString("game"), ChessGame.class));
                        }
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error: invalid game name");
        }
        throw new DataAccessException("Error: invalid game");
    }

    @Override
    public boolean ifGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, " +
                    "blackUsername, gameName, ChessGame FROM game_table")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        if (gameID == rs.getInt("gameID")) {
                            return true;
                        }
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error: invalid game");
        }
        return false;
    }

    @Override
    public HashSet<GameData> getAllGames() throws DataAccessException {
        HashSet<GameData> allGames = new HashSet<>(100);
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, " +
                    "gameName, ChessGame FROM game_table")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        allGames.add(new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                                rs.getString("blackUsername"), rs.getString("gameName"),
                                new Gson().fromJson(rs.getString("game"), ChessGame.class)));

                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error retrieving games");
        }
        return allGames;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            if (game.gameName().matches("[a-zA-Z]+")) {
                var statement = "UPDATE game_table SET whiteUsername = ?, blackUsername = ?, gameName = ?, ChessGame = ? WHERE gameID = ?";
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.setInt(1, game.gameID());
                    if (game.whiteUsername() == null) {
                        preparedStatement.setNull(2, Types.VARCHAR);
                    } else {
                        preparedStatement.setString(2, game.whiteUsername());
                    }
                    if (game.blackUsername() == null) {
                        preparedStatement.setNull(3, Types.VARCHAR);
                    } else {
                        preparedStatement.setString(3, game.blackUsername());
                    }
                    preparedStatement.setString(4, game.gameName());
                    preparedStatement.setString(5, new Gson().toJson(game.game(), ChessGame.class));

                    preparedStatement.executeUpdate();
                }
            } else {
                throw new DataAccessException("Error: invalid game name");
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error updating game");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE TABLE game_table";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error clearing game table");
        }
    }
}
