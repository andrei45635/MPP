package com.example.contest.repo.participantstasks;

import com.example.contest.domain.ParticipantTask;
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

public class ParticipantsTasksDBRepository implements ParticipantsTasksRepository {
    private JDBCutils jdbCutils;
    private static final Logger logger = LogManager.getLogger();

    public ParticipantsTasksDBRepository(Properties props) {
        logger.info("Initializing Repository with {}", props);
        jdbCutils = new JDBCutils(props);
    }
    @Override
    public List<ParticipantTask> getAll() throws IOException {
        logger.traceEntry();
        List<ParticipantTask> participantTasks = new ArrayList<>();

        String query = "SELECT * FROM participantstasks";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    int participantTasksID = resultSet.getInt("participanttasksid");
                    int participantID = resultSet.getInt("participantid");
                    int task1ID = resultSet.getInt("task1id");
                    int task2ID = resultSet.getInt("task2id");
                    ParticipantTask participantTask = new ParticipantTask(participantID, task1ID, task2ID);
                    participantTask.setId(participantTasksID);
                    participantTasks.add(participantTask);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return participantTasks;
    }

    @Override
    public boolean delete(ParticipantTask entity) throws IOException {
        logger.traceEntry("deleting participantstasks {}", entity);

        String query = "DELETE FROM participantstasks WHERE participantstasksid=?";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, entity.getId());
            int result = preparedStatement.executeUpdate();
            logger.traceExit("deleted {}", entity);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return false;
    }

    @Override
    public ParticipantTask update(ParticipantTask entity) throws IOException {
        logger.traceEntry("updating participantstasks {}", entity);

        String query = "UPDATE participantstasks SET participantid=?, taskid1=?, taskid2=? WHERE participantstasksid=?";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, entity.getParticipantID());
            preparedStatement.setInt(2, entity.getTask1ID());
            preparedStatement.setInt(3, entity.getTask2ID());
            preparedStatement.setInt(4, entity.getTask2ID());
            int result = preparedStatement.executeUpdate();
            logger.traceExit("updated participantstasks {}", entity);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return entity;
    }

    @Override
    public ParticipantTask save(ParticipantTask entity) throws IOException {
        logger.traceEntry("saving participantTask {}", entity);

        String query = "INSERT INTO participantstasks(participantid, taskid1, taskid2) VALUES (?, ?, ?)";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setInt(1, entity.getParticipantID());
            preparedStatement.setInt(2, entity.getTask1ID());
            preparedStatement.setInt(3, entity.getTask2ID());
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
        logger.traceEntry("counting participanttasks");

        String query = "SELECT COUNT(*)  FROM participantstasks";
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
}
