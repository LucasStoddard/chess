package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.*;
import java.util.HashSet;


public class SQLGameDAO implements GameDAO {
    private Connection conn;

    public SQLGameDAO(Connection connection) {
        conn = connection;
        var createGameTable = """
                    CREATE TABLE IF NOT EXISTS game_table (
                        gameID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        whiteUsername VARCHAR(255),
                        blackUsername VARCHAR(255),
                        gameName VARCHAR(255) NOT NULL,
                        game TEXT
                    )""";
        try (var createGameTableStatement = conn.prepareStatement(createGameTable)) {
            createGameTableStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error creating game_table");
        }
    }

    @Override
    public void createGame(GameData game) {
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        try {
            var statement = "INSERT INTO game_table (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
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
                if (game.game() == null) {
                    preparedStatement.setNull(5, Types.CLOB); // it was getting mad at Types.TEXT
                } else {
                    preparedStatement.setString(5, new Gson().toJson(game.game(), ChessGame.class));
                }

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("createGame error");;
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, " +
                "gameName, game FROM game_table")) {
            try (var rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    if (gameID == rs.getInt("gameID")) { // these getStrings should just return null if null is there
                        return new GameData(gameID, rs.getString("whiteUsername"), rs.getString("blackUsername"),
                                rs.getString("gameName"), new Gson().fromJson(rs.getString("game"), ChessGame.class));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Invalid game name");;
        }
        throw new DataAccessException("Error: invalid game");
    }

    @Override
    public boolean ifGame(int gameID) throws DataAccessException {
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, " +
                "blackUsername, gameName, game FROM game_table")) {
            try (var rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    if (gameID == rs.getInt("gameID")) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public HashSet<GameData> getAllGames() throws DataAccessException {
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        HashSet<GameData> allGames = new HashSet<>(100);
        try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, " +
                "gameName, game FROM game_table")) {
            try (var rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    allGames.add(new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                            rs.getString("blackUsername"), rs.getString("gameName"),
                            new Gson().fromJson(rs.getString("game"), ChessGame.class)));

                }
            }
        } catch (SQLException e) {
            System.out.println("getAllGames error");
        }
        return allGames;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        try {
            try {
                GameData oldGame = getGame(game.gameID());
            } catch (DataAccessException e) {
                throw new DataAccessException("Error: bad request");
            }
            var statement = "UPDATE game_table SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                if (game.whiteUsername() == null) {
                    preparedStatement.setNull(1, Types.VARCHAR);
                } else {
                    preparedStatement.setString(1, game.whiteUsername());
                }
                if (game.blackUsername() == null) {
                    preparedStatement.setNull(2, Types.VARCHAR);
                } else {
                    preparedStatement.setString(2, game.blackUsername());
                }
                preparedStatement.setString(3, game.gameName());
                preparedStatement.setString(4, new Gson().toJson(game.game(), ChessGame.class));
                preparedStatement.setInt(5, game.gameID());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("updateGame error");
        }
    }

    @Override
    public void clear() {
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        var statement = "TRUNCATE TABLE game_table";
        try (var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("gameClear error");
        }
    }
}
