package edu.brown.cs.azhang6.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A connection to a database.
 *
 * @author aaronzhang
 */
public class Database implements AutoCloseable {

    /**
     * Database file.
     */
    private final String db;

    /**
     * Connection.
     */
    private final Connection conn;

    /**
     * New database.
     *
     * @param db database file
     * @throws ClassNotFoundException if class not found
     * @throws SQLException if sql error
     */
    public Database(String db) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        this.db = db;
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + db);
    }

    /**
     * Performs a query with the given prepared statement and returns a list of
     * results. The list contains the first element of each tuple of results.
     *
     * @param prep prepared statement
     * @return list of results
     */
    public List<String> query(PreparedStatement prep) {
        try (ResultSet rs = prep.executeQuery()) {
            List<String> result = new ArrayList<>();
            while (rs.next()) {
                result.add(rs.getString(1));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Performs a query and applies the function to its result set.
     * 
     * @param <R> return type of function
     * @param prep query
     * @param func function on a result set
     * @return output of function
     * @throws SQLException if sql error
     */
    public <R> R query(PreparedStatement prep, Function<ResultSet, R> func)
        throws SQLException {
        try (ResultSet rs = prep.executeQuery()) {
            return func.apply(rs);
        }
    }
    
    /**
     * Performs a query and applies the consumer to the result set.
     * 
     * @param prep query
     * @param func consumer of result set
     * @throws SQLException if sql error
     */
    public void query(PreparedStatement prep, Consumer<ResultSet> func)
        throws SQLException {
        try (ResultSet rs = prep.executeQuery()) {
            func.accept(rs);
        }
    }

    /**
     * Whether the table contains the tuple (value1, value2).
     *
     * @param table table name
     * @param column1 first column name
     * @param value1 first column value
     * @param column2 second column name
     * @param value2 second column value
     * @return whether table contains tuple
     */
    public boolean has(String table, String column1, String value1,
        String column2, String value2) {
        try (PreparedStatement prep = conn.prepareStatement(String.format(
            "SELECT * FROM %s WHERE %s=? AND %s=?;", table, column1, column2))) {
            prep.setString(1, value1);
            prep.setString(2, value2);
            return !query(prep).isEmpty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return database file
     */
    public String getDb() {
        return db;
    }

    /**
     * @return connection
     */
    public Connection getConn() {
        return conn;
    }

    @Override
    public void close() throws Exception {
        conn.close();
    }
}
