package com.example.contest.repo.participants;

import com.example.contest.domain.Participant;
import com.example.contest.domain.enums.AgeGroup;
import com.example.contest.domain.enums.Type;
import com.example.contest.utils.JDBCutils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ParticipantsDBRepository implements ParticipantsRepository {
    private JDBCutils jdbCutils;
    private static final Logger logger = LogManager.getLogger();

    public ParticipantsDBRepository(Properties props) {
        logger.info("Initializing Repository with {}", props);
        jdbCutils = new JDBCutils(props);
    }

    @Override
    public List<Participant> getAll() throws IOException {
        logger.traceEntry();
        List<Participant> participants = new ArrayList<>();

        String query = "SELECT * FROM participants";
        Connection connection = jdbCutils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int participantID = resultSet.getInt("participantid");
                    String name = resultSet.getString("name");
                    int age = resultSet.getInt("age");
                    Participant participant = new Participant(participantID, name, age);
                    participants.add(participant);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return participants;
    }

    @Override
    public boolean delete(Participant entity) throws IOException {
        logger.traceEntry("deleting participant {}", entity);

        String query = "DELETE FROM participants WHERE participantid=?";
        Connection connection = jdbCutils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
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
    public Participant update(Participant entity) throws IOException {
        logger.traceEntry("updating participant ");

        String query = "UPDATE participants SET name=?, age=? WHERE participantid=?";
        Connection connection = jdbCutils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setInt(2, entity.getAge());
            preparedStatement.setInt(3, entity.getId());
            int result = preparedStatement.executeUpdate();
            logger.traceExit("updated participant {}", entity);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return entity;
    }

    @Override
    public Participant save(Participant entity) throws IOException {
        logger.traceEntry("saving participant {}", entity);

        String query = "INSERT INTO participants(name, age) VALUES (?, ?)";
        Connection connection = jdbCutils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setInt(2, entity.getAge());
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
        logger.traceEntry("counting participants");

        String query = "SELECT COUNT(*)  FROM participants";
        Connection connection = jdbCutils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
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
    public Participant findByID(int id) {
        String query = "SELECT * FROM participants WHERE participantid=?";
        Connection connection = jdbCutils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int participantID = resultSet.getInt("participantid");
                    String name = resultSet.getString("name");
                    int age = resultSet.getInt("age");
                    return new Participant(participantID, name, age);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return null;
    }

    @Override
    public List<Participant> filterByAgeType(Type type, AgeGroup ageGroup) {
        List<Participant> filteredParticipants = new ArrayList<>();
        String query = """
                select p.participantid, p.name, p.age from participants p  INNER JOIN participantstasks pt on p.participantid=pt.participantID
                                                      INNER JOIN tasks t on pt.taskID1=t.taskid OR pt.taskID2 = t.taskid
                where t.type=? AND t.agegroup=?""";
        Connection connection = jdbCutils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, String.valueOf(type));
            preparedStatement.setString(2, String.valueOf(ageGroup));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int participantID = resultSet.getInt("participantid");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                Participant participant = new Participant(participantID, name, age);
                filteredParticipants.add(participant);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return filteredParticipants;
    }
}
