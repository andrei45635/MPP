package com.example.protobuff;

import com.example.*;
import com.example.dto.TaskDTO;
import com.example.dto.ParticipantDTO;
import com.example.enums.AgeGroup;
import com.example.enums.Type;
import com.example.network.Request;
import com.example.network.RequestType;
import com.example.network.Response;
import com.example.network.ResponseType;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ContestProtoProxy implements IContestService {
    private String host;
    private int port;
    private IContestObserver client;
    private InputStream input;
    private OutputStream output;
    private Socket connection;
    private BlockingQueue<ContestProto.ContestResponse> qresponses;
    private volatile boolean finished;

    public ContestProtoProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<ContestProto.ContestResponse>();
    }

    @Override
    public void login(User user, IContestObserver client) throws ContestException, IOException {
        initializeConnection();
        System.out.println("Creating and sending login request...");
        sendRequest(ProtoUtils.createLoginRequest(user));
        System.out.println("Reading the response...");
        ContestProto.ContestResponse response = readResponse();
        System.out.println("Read the response...");
        if (response.getRep() == ContestProto.ContestResponse.Reply.OK) {
            this.client = client;
            return;
        }
        if (response.getRep() == ContestProto.ContestResponse.Reply.Error) {
            String errorText = ProtoUtils.getError(response);
            closeConnection();
            throw new ContestException(errorText);
        }
    }

    @Override
    public void logout(User user, IContestObserver client) throws ContestException {
        System.out.println("Creating and sending logout request...");
        sendRequest(ProtoUtils.createLogoutRequest(user));
        ContestProto.ContestResponse response = readResponse();
        System.out.println("Reading the response...");
        closeConnection();
        if (response.getRep() == ContestProto.ContestResponse.Reply.Error) {
            String errorText = ProtoUtils.getError(response);
            throw new ContestException(errorText);
        }
    }

    private List<Participant> model2protoParticipantMapper(List<ContestProto.ParticipantDTO> parts) {
        List<Participant> res = new ArrayList<>();
        for(var tk: parts){
            Participant p = new Participant(new Random().nextInt(1488 - 500) + 500, tk.getName(), tk.getAge());
            res.add(p);
        }
        return res;
    }

    @Override
    public List<Participant> getFilteredParticipants(Type type, AgeGroup ageGroup) throws ContestException {
        System.out.println("Creating and sending a request to filter participants...");
        sendRequest(ProtoUtils.createGetFilteredParticipantsRequest(type, ageGroup));
        System.out.println("Reading the response...");
        ContestProto.ContestResponse response = readResponse();
        if(response.getRep() == ContestProto.ContestResponse.Reply.FILTER){
            return model2protoParticipantMapper(ProtoUtils.getFilteredPartsDTO(response));
        } else if(response.getRep() == ContestProto.ContestResponse.Reply.Error){
            String error = response.getError() + " eroare";
            throw new ContestException(error);
        }
        return null;
    }

    @Override
    public int getParticipantSize() throws ContestException {
        return 0;
    }

    @Override
    public void register(String name, int age, Type type1, Type type2) throws ContestException, IOException {
        System.out.println("Creating and sending request to add participant... ");
        sendRequest(ProtoUtils.createRegisterRequest(name, age, type1, type2));
        System.out.println("Reading the response...");
        ContestProto.ContestResponse response = readResponse();
        if (response.getRep() == ContestProto.ContestResponse.Reply.Error) {
            String error = response.getError() + " eroare";
            throw new ContestException(error);
        }
    }

    @Override
    public List<Task> getAllTasks() throws ContestException, IOException {
        return null;
    }

    @Override
    public boolean checkUserExists(String username, String password) throws ContestException {
        return false;
    }

    @Override
    public User findLoggedInUser(String username, String password) throws ContestException, IOException {
        return null;
    }

    @Override
    public List<Task> getAllTasksWithMaxEnrolled() throws ContestException {
        return null;
    }

    @Override
    public int getEnrolled(AgeGroup ageGroup, Type type) throws ContestException {
        return 0;
    }

    private List<TaskDTO> model2protoMapper(List<ContestProto.TaskDTO> tasks) {
        List<TaskDTO> res = new ArrayList<>();
        for(var tk: tasks){
            TaskDTO taskDTO = new TaskDTO(tk.getTaskID(), Type.valueOf(tk.getType().toString()), AgeGroup.valueOf(tk.getAge().toString()), tk.getEnrolled());
            res.add(taskDTO);
        }
        return res;
    }

    @Override
    public List<TaskDTO> getTasksEnrolled() throws ContestException {
        System.out.println("Creating and sending tasks enrolled request...");
        sendRequest(ProtoUtils.createGetTasksEnrolledRequest());
        ContestProto.ContestResponse response = readResponse();
        if (response.getRep() == ContestProto.ContestResponse.Reply.GET_ALL_TASKS_EXPERIMENTAL) {
            return model2protoMapper(ProtoUtils.getEnrolledTasks(response));
        } else if (response.getRep() == ContestProto.ContestResponse.Reply.Error) {
            String error = response.getError();
            throw new ContestException(error);
        }
        return null;
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = connection.getOutputStream();
            input = connection.getInputStream();
            finished = false;
            startReader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private void sendRequest(ContestProto.ContestRequest request) throws ContestException {
        try {
            System.out.println("Sending request " + request);
            request.writeDelimitedTo(output);
            output.flush();
        } catch (Exception e) {
            throw new ContestException("Error sending object " + e);
        }
    }

    private ContestProto.ContestResponse readResponse() throws ContestException {
        ContestProto.ContestResponse response;
        try {
            response = qresponses.take();
        } catch (InterruptedException e) {
            throw new ContestException("Error sending object " + e);
        }
        return response;
    }

    private void handleUpdate(ContestProto.ContestResponse response) {
        if (response.getRep() == ContestProto.ContestResponse.Reply.UPDATE) {
            System.out.println("before update");
            try {
                client.updateTaskList();
            } catch (ContestException e) {
                throw new RuntimeException(e);
            }
            System.out.println("after update");
        }
    }

    private boolean isUpdate(ContestProto.ContestResponse response) {
        return response.getRep() == ContestProto.ContestResponse.Reply.UPDATE;
    }

    private class ReaderThread implements Runnable {
        @Override
        public void run() {
            while (!finished) {
                try {
                    ContestProto.ContestResponse response = ContestProto.ContestResponse.parseDelimitedFrom(input);
                    System.out.println("Response received " + response);
                    if (isUpdate(response)) {
                        System.out.println("i'm an update" + response);
                        handleUpdate(response);
                    } else {
                        try {
                            qresponses.put(response);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
