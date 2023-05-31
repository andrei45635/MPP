package com.example.repo.participantstasks;

import com.example.ParticipantTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.util.List;


public class ParticipantsTasksDBRepository implements ParticipantsTasksRepository {
    private static final Logger logger = LogManager.getLogger();
    private static SessionFactory sessionFactory;

    public static SessionFactory getSession() {
        logger.traceEntry();
        try {
            if (sessionFactory == null || sessionFactory.isClosed())
                sessionFactory = getNewSession();
        } catch (Exception e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit(sessionFactory);
        return sessionFactory;
    }

    public static SessionFactory getNewSession() {
        SessionFactory ses = null;
        try {
            final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure() // configures settings from hibernate.cfg.xml
                    .build();
            ses = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            logger.error(e);
            System.out.println("Error getting connection " + e);
        }
        return ses;
    }

    public ParticipantsTasksDBRepository() {
    }

    public static void initialize() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Exception " + e);
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    public static void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Override
    public List<ParticipantTask> getAll() {
        logger.traceEntry("getting all the participant tasks");
        SessionFactory ses = getSession();
        try (Session session = ses.openSession()) {
            Transaction transact = null;
            try {
                transact = session.beginTransaction();
                List<ParticipantTask> ptasks = session.createQuery("FROM ParticipantTask ", ParticipantTask.class).list();
                System.out.println(ptasks.size() + " participant tasks");
                transact.commit();
                return ptasks;
            } catch (Exception e) {
                System.err.println("Error when getting all the participant tasks " + e);
                if (transact != null) {
                    transact.rollback();
                }
            }
        }
        return null;
    }

    @Override
    public boolean delete(ParticipantTask entity) {
        logger.traceEntry("deleting participantstasks {}", entity);
        SessionFactory ses = getSession();
        try (Session session = ses.openSession()) {
            Transaction transact = null;
            try {
                transact = session.beginTransaction();
                String hql = "DELETE FROM ParticipantTask WHERE cast(id AS string) LIKE :participantstasksid";
                Query query = session.createQuery(hql);
                query.setParameter("participantstasksid", entity.getId().toString());
                int result = query.executeUpdate();
                System.out.println("Rows affected " + result);
                transact.commit();
            } catch (Exception e) {
                System.err.println("Error when deleting the participant task " + e);
                if (transact != null) {
                    transact.rollback();
                }
            }
        }
        return false;
    }

    @Override
    public ParticipantTask update(ParticipantTask entity) {
        logger.traceEntry("updating participantstasks {}", entity);
        SessionFactory ses = getSession();
        try (Session session = ses.openSession()) {
            Transaction transact = null;
            try {
                transact = session.beginTransaction();
                String hql = "UPDATE ParticipantTask SET participantID=:participantid, task1ID=:taskid1, task2ID=:taskid2 WHERE id=:ptid";
                Query query = session.createQuery(hql);
                query.setParameter("participantid", entity.getParticipantID());
                query.setParameter("taskid1", entity.getTask1ID());
                query.setParameter("taskid2", entity.getTask2ID());
                query.setParameter("ptid", entity.getId());
                int result = query.executeUpdate();
                System.out.println("Rows affected " + result);
                transact.commit();
            } catch (Exception e) {
                System.err.println("Error when updating the participant tasks " + e);
                if (transact != null) {
                    transact.rollback();
                }
            }
        }
        return entity;
    }

    @Override
    public ParticipantTask save(ParticipantTask entity) {
        SessionFactory ses = getSession();
        logger.traceEntry("saving participantstasks {}", entity);
        try (Session session = ses.openSession()) {
            Transaction transact = null;
            try {
                transact = session.beginTransaction();
                session.save(entity);
                transact.commit();
            } catch (Exception e) {
                System.err.println("Error when updating the participant tasks " + e);
                if (transact != null) {
                    transact.rollback();
                }
            }
        }
        return entity;
    }

    @Override
    public int size() {
        logger.traceEntry("size of participantstasks");
        SessionFactory ses = getSession();
        try (Session session = ses.openSession()) {
            Transaction transact = null;
            try {
                transact = session.beginTransaction();
                int count = ((Long) session.createQuery("SELECT COUNT(*) FROM ParticipantTask").uniqueResult()).intValue();
                transact.commit();
                return count;
            } catch (Exception e) {
                System.err.println("Error when updating the participant tasks " + e);
                if (transact != null) {
                    transact.rollback();
                }
            }
        }
        return 0;
    }

    /*public ParticipantsTasksDBRepository(Properties props) {
        logger.info("Initializing Repository with {}", props);
        jdbCutils = new JDBCutils(props);
    }

    @Override
    public List<ParticipantTask> getAll() {
        logger.traceEntry();
        List<ParticipantTask> participantTasks = new ArrayList<>();

        String query = "SELECT * FROM participantstasks";
        Connection connection = jdbCutils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
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
    public ParticipantTask update(ParticipantTask entity) throws IOException {
        logger.traceEntry("updating participantstasks {}", entity);

        String query = "UPDATE participantstasks SET participantid=?, taskid1=?, taskid2=? WHERE participantstasksid=?";
        Connection connection = jdbCutils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
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

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
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
    }*/
}
