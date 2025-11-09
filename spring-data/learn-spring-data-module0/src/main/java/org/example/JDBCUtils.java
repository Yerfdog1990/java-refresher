package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

public class JDBCUtils {
    private static Connection createConnection() throws SQLException {
        String username = "root";
        String password = "";
        String url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
        return DriverManager.getConnection(url,username,password);
    }
    public static void doWithConnection(Consumer<Connection> action) throws SQLException{
        try(Connection connection = createConnection()){
            action.accept(connection);
        }
    }
    public static void doWithStatement(Consumer<Statement> action) throws SQLException{
        doWithConnection(connection -> {
            try(Statement statement = connection.createStatement()){
                action.accept(statement);
            } catch (SQLException e) {
                System.err.println("Failed to create statement: " + e.getMessage());
            }
        });
    }
    public static void createTable(Statement statement) {
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(50),
                    email VARCHAR(100),
                    grade INT
                );
                """;
        try {
            statement.executeUpdate(createTableSQL);
            System.out.println("Table created successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to create table: " + e.getMessage());
        }
    }
}
