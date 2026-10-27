package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    public UserDaoJDBCImpl() {

    }

    public void createUsersTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(50),
                    lastName VARCHAR(50),
                    age TINYINT
                )
                """;

        try(
                Connection connection = Util.getConnection();
                Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to create table users", e);
        }
    }

    public void dropUsersTable() {
        String sql = """
                DROP TABLE IF EXISTS users
                """;
        try (
                Connection connection = Util.getConnection();
                Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to drop table users", e);
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must be not a blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("lastName must be not a blank");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("age must be more than 0");
        }

        String sql = """
                INSERT INTO users(name, lastName, age)
                VALUES (?, ?, ?)
                """;
        try (
                Connection connection = Util.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, name);
            statement.setString(2, lastName);
            statement.setByte(3, age);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to save user", e);
        }
    }

    public void removeUserById(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("id must be more than 0");
        }
        String sql = """
                DELETE FROM users
                WHERE id = ?
                """;
        try (
                Connection connection = Util.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete user with id = "+ id , e);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, lastName, age FROM users ";
        try (
                Connection connection = Util.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
        ) {
           while(resultSet.next()) {
               User user = new User();

               user.setId(resultSet.getLong("id"));
               user.setName(resultSet.getString("name"));
               user.setLastName(resultSet.getString("lastName"));

               byte age = resultSet.getByte("age");
               user.setAge(resultSet.wasNull() ? null : age);

               users.add(user);
           }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to get list of users", e);
        }

        return users;
    }

    public void cleanUsersTable() {
        String sql = "DELETE FROM users";

        try (
                Connection connection = Util.getConnection();
                Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to clear table users", e);
        }
    }
}
