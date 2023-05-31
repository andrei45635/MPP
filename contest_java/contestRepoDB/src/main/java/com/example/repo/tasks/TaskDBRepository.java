package com.example.repo.tasks;

import com.example.Task;
import com.example.enums.AgeGroup;
import com.example.enums.Type;
import com.example.utils.JDBCutils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class TaskDBRepository implements TaskRepository {
    private JDBCutils jdbCutils;
    private static final Logger logger = LogManager.getLogger();

    public TaskDBRepository(Properties props) {
        logger.info("Initializing Repository with {}", props);
        jdbCutils = new JDBCutils(props);
    }
    @Override
    public List<Task> getAll(){
        logger.traceEntry();
        List<Task> tasks = new ArrayList<>();

        String query = "SELECT * FROM tasks";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    int taskID = resultSet.getInt("taskid");
                    Type type = Type.valueOf(resultSet.getString("type"));
                    AgeGroup agegroup = AgeGroup.valueOf(resultSet.getString("agegroup"));
                    Task task = new Task(taskID, type, agegroup);
                    tasks.add(task);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return tasks;
    }

    @Override
    public boolean delete(Task entity) throws IOException {
        logger.traceEntry("deleting tasks {}", entity);

        String query = "DELETE FROM tasks WHERE taskid=?";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, entity.getId());
            int result = preparedStatement.executeUpdate();
            if(result > 0) {
                return true;
            }
            logger.traceExit("deleted {}", entity);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return false;
    }

    @Override
    public Task update(Task entity) throws IOException {
        logger.traceEntry("updating tasks {}", entity);

        String query = "UPDATE tasks SET type=?, agegroup=? WHERE taskid=?";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, String.valueOf(entity.getType()));
            preparedStatement.setString(2, String.valueOf(entity.getAgeGroup()));
            preparedStatement.setInt(3, entity.getId());
            preparedStatement.executeUpdate();
            logger.traceExit("updated task {}", entity);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return entity;
    }

    private Task findMaxID(){
        String query = "SELECT * FROM tasks where taskid=(SELECT max(taskid) FROM tasks)";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    int taskID = resultSet.getInt("taskid");
                    Type type = Type.valueOf(resultSet.getString("type"));
                    AgeGroup agegroup = AgeGroup.valueOf(resultSet.getString("agegroup"));
                    return new Task(taskID, type, agegroup);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public Task save(Task entity) throws IOException {
        logger.traceEntry("saving task {}", entity);

        String query = "INSERT INTO tasks(type, agegroup) VALUES (?, ?)";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, entity.getType().toString());
            preparedStatement.setString(2, entity.getAgeGroup().toString());
            int result = preparedStatement.executeUpdate();
            if(result > 0){
                return findMaxID();
            }
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
        logger.traceEntry("counting tasks");

        String query = "SELECT COUNT(*)  FROM tasks";
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
    public List<Task> findTaskByAge(AgeGroup age) {
        List<Task> tasks = new ArrayList<>();

        String query = "SELECT * FROM tasks where agegroup=?";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, String.valueOf(age));
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    int taskID = resultSet.getInt("taskid");
                    Type type = Type.valueOf(resultSet.getString("type"));
                    AgeGroup agegroup = AgeGroup.valueOf(resultSet.getString("agegroup"));
                    Task task = new Task(taskID, type, agegroup);
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tasks;
    }

    @Override
    public List<Task> findTaskByType(Type type) {
        List<Task> tasks = new ArrayList<>();

        String query = "SELECT * FROM tasks where type=?";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, String.valueOf(type));
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    int taskID = resultSet.getInt("taskid");
                    Type typeD = Type.valueOf(resultSet.getString("type"));
                    AgeGroup agegroup = AgeGroup.valueOf(resultSet.getString("agegroup"));
                    Task task = new Task(taskID, typeD, agegroup);
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tasks;
    }

    @Override
    public Task findById(int id) {
        String query = "SELECT * FROM tasks where taskid=?";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    int taskID = resultSet.getInt("taskid");
                    Type type = Type.valueOf(resultSet.getString("type"));
                    AgeGroup agegroup = AgeGroup.valueOf(resultSet.getString("agegroup"));
                    return new Task(taskID, type, agegroup);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public int getTasksByAgeType(AgeGroup age, Type type) {
        String query = "SELECT COUNT(*) FROM tasks t INNER JOIN participantstasks pt ON pt.taskID2 = t.taskid OR pt.taskID1 = t.taskid\n" +
                "                             INNER JOIN participants p on p.participantid = pt.participantID\n" +
                " WHERE type=? AND agegroup=?";
        Connection connection = jdbCutils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, String.valueOf(type));
            preparedStatement.setString(2, String.valueOf(age));
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                logger.traceExit(resultSet.getInt("count(*)"));
                return resultSet.getInt("count(*)");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public boolean deleteById(int id) {
        logger.traceEntry("deleting task by id {}", id);

        String query = "DELETE FROM tasks WHERE taskid=?";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            logger.traceExit("deleted {}");
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }
        return false;
    }
}
