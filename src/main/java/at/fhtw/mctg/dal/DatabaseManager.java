package at.fhtw.mctg.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DatabaseManager {
    INSTANCE;

    public Connection getConnection()
    {
        try {
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/mctgdb?currentSchema=mctg",
                    "mctguser",
                    "mctgpw");
        } catch (SQLException e) {
            throw new DataAccessException("Database connection could not be established", e);
        }
    }
}
