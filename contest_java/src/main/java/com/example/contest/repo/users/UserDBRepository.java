package com.example.contest.repo.users;

import com.example.contest.domain.User;
import com.example.contest.utils.JDBCutils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class UserDBRepository implements UserRepository{
    private JDBCutils jdbCutils;
    private static final Logger logger = LogManager.getLogger();

    public UserDBRepository(Properties props) {
        logger.info("Initializing Repository with {}", props);
        jdbCutils = new JDBCutils(props);
    }

    @Override
    public List<User> getAll() throws IOException {
        logger.traceEntry();
        List<User> users = new ArrayList<>();

        String query = "SELECT * FROM users";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    int userID = resultSet.getInt("userid");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    User user = new User(userID, username, password);
                    //user.setId(userID);
                    users.add(user);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return users;
    }

    @Override
    public boolean delete(User entity) throws IOException {
        logger.traceEntry("deleting user {}", entity);

        String query = "DELETE FROM users WHERE userid=?";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, entity.getId());
            preparedStatement.executeUpdate();
            logger.traceExit("deleted {}", entity);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return false;
    }

    @Override
    public User update(User entity) throws IOException {
        logger.traceEntry("updating user ");

        String query = "UPDATE users SET username=?, password=? WHERE userid=?";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, entity.getUsername());
            preparedStatement.setString(2, entity.getPassword());
            preparedStatement.setInt(3, entity.getId());
            int result = preparedStatement.executeUpdate();
            logger.traceExit("updated user {}", entity);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return entity;
    }

    @Override
    public User save(User entity) throws IOException {
        logger.traceEntry("saving user {}", entity);

        String query = "INSERT INTO users(userid, username, password) VALUES (?, ?, ?)";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setInt(1, entity.getId());
            preparedStatement.setString(2, entity.getUsername());
            preparedStatement.setString(3, entity.getPassword());
            int result = preparedStatement.executeUpdate();
            logger.trace("Saved {}", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return entity;
    }

    @Override
    public int size() {
        logger.traceEntry("counting users");

        String query = "SELECT COUNT(*)  FROM users";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    logger.traceExit(resultSet.getInt("count(*)"));
                    return resultSet.getInt("count(*)");
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return 0;
    }

    @Override
    public boolean findUser(String username, String password) {
        logger.traceEntry("checking if user with given parameters exists");
        String query = "SELECT EXISTS (SELECT 1 FROM users WHERE username = ? AND password = ?)";
        Connection connection = jdbCutils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next() && resultSet.getInt(1) == 1){
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
