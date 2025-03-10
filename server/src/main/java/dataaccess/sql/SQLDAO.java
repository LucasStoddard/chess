package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import chess.ChessGame;
import com.google.gson.Gson;

import javax.xml.crypto.Data;
import java.sql.*;


public abstract class SQLDAO {
    public SQLDAO() throws DataAccessException {
        setUpDatabase();
    }




    private void setUpDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
    }
}
