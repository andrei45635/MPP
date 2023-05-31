package com.example.service;

import com.example.*;
import com.example.dto.TaskDTO;
import com.example.enums.AgeGroup;
import com.example.enums.Type;
import com.example.repo.participants.ParticipantsRepository;
import com.example.repo.participantstasks.ParticipantsTasksDBRepository;
import com.example.repo.tasks.TaskRepository;
import com.example.repo.users.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContestService implements IContestService {
    private UserRepository userRepository;
    private TaskRepository taskRepository;
    private ParticipantsRepository participantsRepository;
    private ParticipantsTasksDBRepository participantsTasksRepository;
    private Map<String, IContestObserver> loggedInClients;

    public ContestService(UserRepository userRepository, TaskRepository taskRepository, ParticipantsRepository participantsRepository, ParticipantsTasksDBRepository participantsTasksRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.participantsRepository = participantsRepository;
        this.participantsTasksRepository = participantsTasksRepository;
        loggedInClients = new ConcurrentHashMap<>();
    }

    private void notifyObservers() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (var c : loggedInClients.entrySet()) {
            IContestObserver obs = loggedInClients.get(c.getKey());
            System.out.println("observer " + obs);
            if (obs != null) {
                executorService.execute(() -> {
                    try {
                        System.out.println("Updating task list...");
                        obs.updateTaskList();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        executorService.shutdown();
    }

    @Override
    public List<Participant> getFilteredParticipants(Type type, AgeGroup ageGroup) {
        return participantsRepository.filterByAgeType(type, ageGroup);
    }

    public int getParticipantSize() {
        return participantsRepository.size();
    }

    @Override
    public synchronized List<Task> getAllTasksWithMaxEnrolled() {
        return new ArrayList<>(this.getAllTasks());
    }

    @Override
    public int getEnrolled(AgeGroup ageGroup, Type type) {
        return taskRepository.getTasksByAgeType(ageGroup, type);
    }

    @Override
    public synchronized List<TaskDTO> getTasksEnrolled() throws ContestException {
        List<TaskDTO> allOfThem = new ArrayList<>();
        for (var tk : taskRepository.getAll()) {
            allOfThem.add(new TaskDTO(tk.getId(), tk.getType(), tk.getAgeGroup(), taskRepository.getTasksByAgeType(tk.getAgeGroup(), tk.getType())));
        }
        return allOfThem;
    }

    @Override
    public synchronized void register(String name, int age, Type type1, Type type2) throws IOException {
        Participant participant = new Participant(participantsRepository.size() + 1, name, age);
        Task task1 = null, task2 = null;
        if (age > 6 && age <= 9) {
            task1 = new Task(taskRepository.size() + 1, type1, AgeGroup.YOUNG);
            task2 = new Task(taskRepository.size() + 2, type2, AgeGroup.YOUNG);
        } else if (age >= 10 && age <= 11) {
            task1 = new Task(taskRepository.size() + 1, type1, AgeGroup.PRETEEN);
            task2 = new Task(taskRepository.size() + 2, type2, AgeGroup.PRETEEN);
        } else if (age >= 12 && age < 15) {
            task1 = new Task(taskRepository.size() + 1, type1, AgeGroup.TEEN);
            task2 = new Task(taskRepository.size() + 2, type2, AgeGroup.TEEN);
        }
        assert task1 != null;
        ParticipantsTasksDBRepository.initialize();
        int size = participantsTasksRepository.size();
        ParticipantTask participantTask = new ParticipantTask(size + 2, participant.getId(), task1.getId(), task2.getId());
        participantsRepository.save(participant);
        taskRepository.save(task1);
        taskRepository.save(task2);
        participantsTasksRepository.save(participantTask);
        ParticipantsTasksDBRepository.close();
        notifyObservers();
    }

    public List<Task> getAllTasks() {
        return taskRepository.getAll();
    }

    public boolean checkUserExists(String username, String password) {
        return userRepository.findUser(username, password);
    }

    public synchronized User findLoggedInUser(String username, String password) {
        for (User u : userRepository.getAll()) {
            if (Objects.equals(u.getUsername(), username) && Objects.equals(u.getPassword(), password)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public synchronized void login(User user, IContestObserver client) throws ContestException {
        User loggedUser = findLoggedInUser(user.getUsername(), user.getPassword());
        if (loggedUser != null) {
            if (loggedInClients.containsKey(loggedUser.getUsername())) {
                throw new ContestException("User is already logged in!");
            }
            loggedInClients.put(loggedUser.getUsername(), client);
            System.out.println("logged in");
        } else {
            throw new ContestException("Authentication failed.");
        }
    }

    @Override
    public synchronized void logout(User user, IContestObserver client) throws ContestException {
        IContestObserver localClient = loggedInClients.remove(user.getUsername());
        if (localClient == null)
            throw new ContestException("User " + user.getId() + " is not logged in!");
    }
}
