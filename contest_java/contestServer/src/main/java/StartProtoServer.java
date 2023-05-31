import com.example.IContestService;
import com.example.repo.participants.ParticipantsDBRepository;
import com.example.repo.participants.ParticipantsRepository;
import com.example.repo.participantstasks.ParticipantsTasksDBRepository;
import com.example.repo.participantstasks.ParticipantsTasksRepository;
import com.example.repo.tasks.TaskDBRepository;
import com.example.repo.tasks.TaskRepository;
import com.example.repo.users.UserDBRepository;
import com.example.repo.users.UserRepository;
import com.example.service.ContestService;
import com.example.utils.AbstractServer;
import com.example.utils.ContestProtoConcurrentServer;
import com.example.utils.ServerException;

import java.io.IOException;
import java.util.Properties;

public class StartProtoServer {
    private static int defaultPort = 55555;

    public static void main(String[] args) {
        Properties serverProps = new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/contestServer.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find contestServer.properties " + e);
            return;
        }
        UserRepository userRepository = new UserDBRepository(serverProps);
        TaskRepository taskRepository = new TaskDBRepository(serverProps);
        ParticipantsRepository participantsRepository = new ParticipantsDBRepository(serverProps);
        //ParticipantsTasksRepository participantsTasksRepository = new ParticipantsTasksDBRepository(serverProps);
        ParticipantsTasksDBRepository participantsTasksRepository = new ParticipantsTasksDBRepository();
        IContestService contestServer = new ContestService(userRepository, taskRepository, participantsRepository, participantsTasksRepository);
        int contestServerPort = defaultPort;
        try {
            contestServerPort = Integer.parseInt(serverProps.getProperty("contest.server.port"));
        } catch (NumberFormatException nef) {
            System.err.println("Wrong  Port Number" + nef.getMessage());
            System.err.println("Using default port " + defaultPort);
        }
        System.out.println("Starting server on port: " + contestServerPort);
        AbstractServer server = new ContestProtoConcurrentServer(contestServerPort, contestServer);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }
    }
}
