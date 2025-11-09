package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

import static org.example.JDBCUtils.createTable;
import static org.example.JDBCUtils.doWithStatement;

public class CRUD_Demo {
    public static void main(String[] args) throws SQLException {
        Consumer<Statement> consumerStatement = statement -> {
            createTable(statement);
            // Create data
            createData(statement);
            // Read data
            readData(statement);
            // Update data
            updateData(statement);
            // Delete data
            deleteData(statement);
        };
        doWithStatement(consumerStatement);
    }

    private static void deleteData(Statement statement) {
        String deleteDataQuery = """
                DELETE FROM users WHERE name = 'Hezbon Lam';
                """;
        try {
            statement.executeUpdate(deleteDataQuery);
            System.out.println("Data deleted successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to delete user: " + e.getMessage());
        }
    }

    private static void updateData(Statement statement) {
        String updateDataQuery = """
                UPDATE users SET grade = 13 WHERE name = 'Jane Smith';
                """;
        try {
            statement.executeUpdate(updateDataQuery);
            System.out.println("Data updated successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to update user: " + e.getMessage());
        }
    }

    private static void readData(Statement statement) {
        String readDataQuery = """
                SELECT * FROM users;
                """;
        try (ResultSet resultSet = statement.executeQuery(readDataQuery)) {
            System.out.println("List of users:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                int grade = resultSet.getInt("grade");
                System.out.printf("User %d: %s (%s), grade: %d", id, name, email, grade);
                System.out.println();
            }
        } catch (SQLException e) {
            System.err.println("Failed to read user: " + e.getMessage());
        }
    }

    private static void createData(Statement statement) {
        String createDataQuery = """
                INSERT INTO users (name, email, grade)
                VALUES ('John Doe', 'john.doe@example.com', 10),
                       ('Jane Smith', 'jane.smith@example.com', 12),
                       ('Hezbon Lam', 'hezbon.lam@example.com', 11);
                """;
        try {
            statement.executeUpdate(createDataQuery);
            System.out.println("Data created successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to create user: " + e.getMessage());
        }
    }

}