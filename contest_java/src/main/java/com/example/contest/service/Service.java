package com.example.contest.service;

import com.example.contest.domain.Participant;
import com.example.contest.domain.ParticipantTask;
import com.example.contest.domain.Task;
import com.example.contest.domain.User;
import com.example.contest.domain.enums.AgeGroup;
import com.example.contest.domain.enums.Type;
import com.example.contest.repo.participants.ParticipantsRepository;
import com.example.contest.repo.participantstasks.ParticipantsTasksRepository;
import com.example.contest.repo.tasks.TaskRepository;
import com.example.contest.repo.users.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Service {
    private UserRepository userRepository;
    private TaskRepository taskRepository;
    private ParticipantsRepository participantsRepository;
    private ParticipantsTasksRepository participantsTasksRepository;

    public Service(UserRepository userRepository, TaskRepository taskRepository, ParticipantsRepository participantsRepository, ParticipantsTasksRepository participantsTasksRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.participantsRepository = participantsRepository;
        this.participantsTasksRepository = participantsTasksRepository;
    }

    public List<Participant> getFilteredParticipants(Type type, AgeGroup ageGroup) throws IOException {
        return participantsRepository.filterByAgeType(type, ageGroup);
    }

    public int getParticipantSize(){
        return participantsRepository.size();
    }

    public int getTaskSize(){
        return taskRepository.size();
    }

    public void addParticipant(Participant participant) throws IOException {
        participantsRepository.save(participant);
    }

    public void addTask(Task task) throws IOException {
        taskRepository.save(task);
    }

    public void register(String name, int age, Type type1, Type type2) throws IOException {
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
        ParticipantTask participantTask = new ParticipantTask(participant.getId(), task1.getId(), task2.getId());
        participantsRepository.save(participant);
        taskRepository.save(task1);
        taskRepository.save(task2);
        participantsTasksRepository.save(participantTask);
    }

    public void addParticipantTask(ParticipantTask participantTask) throws IOException {
        participantsTasksRepository.save(participantTask);
    }

    public List<Task> getAllTasks() throws IOException {
        return taskRepository.getAll();
    }

    public boolean checkUserExists(String username, String password){
        return userRepository.findUser(username, password);
    }

    public User findLoggedInUser(String username, String password) throws IOException {
        for(User u: userRepository.getAll()){
            if(Objects.equals(u.getUsername(), username) && Objects.equals(u.getPassword(), password)){
                return u;
            }
        }
        return null;
    }
}
