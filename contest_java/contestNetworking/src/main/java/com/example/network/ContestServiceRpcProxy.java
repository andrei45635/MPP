package com.example.network;

import com.example.*;
import com.example.dto.TaskDTO;
import com.example.enums.AgeGroup;
import com.example.enums.Type;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ContestServiceRpcProxy implements IContestService {
    private String host;
    private int port;
    private IContestObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ContestServiceRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<Response>();
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
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

    private void sendRequest(Request request) throws ContestException {
        try {
            System.out.println(output);
            output.writeObject(request);
            output.flush();
        } catch (Exception e) {
            throw new ContestException("Error sending object " + e);
        }
    }

    private Response readResponse() throws ContestException {
        Response response;
        try {
            response = qresponses.take();
        } catch (InterruptedException e) {
            throw new ContestException("Error sending object " + e);
        }
        return response;
    }

    @Override
    public void login(User user, IContestObserver client) throws ContestException {
        initializeConnection();
        Request request = new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            this.client = client;
            return;
        }
        if (response.type() == ResponseType.ERROR) {
            String error = response.data().toString();
            closeConnection();
            throw new ContestException(error);
        }
    }

    @Override
    public void logout(User user, IContestObserver client) throws ContestException {
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(user).build();
        sendRequest(request);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            String error = response.data().toString();
            throw new ContestException(error);
        }
    }

    @Override
    public List<Task> getAllTasksWithMaxEnrolled() throws ContestException {
        return null;
    }

    @Override
    public int getEnrolled(AgeGroup ageGroup, Type type) throws ContestException {
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TaskDTO> getTasksEnrolled() throws ContestException {
        Request request = new Request.Builder().type(RequestType.GET_ALL_TASKS_EXPERIMENTAL).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.GET_ALL_TASKS_EXPERIMENTAL) {
            return (List<TaskDTO>) response.data();
        }
        else if (response.type() == ResponseType.ERROR) {
            String error = response.data().toString();
            throw new ContestException(error);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Participant> getFilteredParticipants(Type type, AgeGroup ageGroup) throws ContestException {
        String[] filters = new String[2];
        filters[0] = String.valueOf(type);
        filters[1] = String.valueOf(ageGroup);
        Request request = new Request.Builder().type(RequestType.FILTER).data(filters).build();
        System.out.println("Sending request to filter participants...");
        sendRequest(request);
        System.out.println("Reading the response...");
        Response response = readResponse();
        if(response.type() == ResponseType.FILTER){
            return (List<Participant>) response.data();
        } else if(response.type() == ResponseType.ERROR){
            String error = response.data().toString() + " eroare";
            throw new ContestException(error);
        }
        return null;
    }

    @Override
    public int getParticipantSize() throws ContestException {
        return 0;
    }

    public void register(String name, int age, Type type1, Type type2) throws ContestException {
        ParticipantTask participantTask = new ParticipantTask(name, age, type1, type2);
        Request request = new Request.Builder().type(RequestType.ADD_PARTICIPANT_EXPERIMENTAL).data(participantTask).build();
        System.out.println("Sending request to add participant... ");
        sendRequest(request);
        System.out.println("Reading the response...");
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String error = response.data().toString() + " eroare";
            throw new ContestException(error);
        }
        //client.updateTaskList();
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
    public User findLoggedInUser(String username, String password) throws ContestException {
        return null;
    }

    private void handleUpdate(Response response) {
        if (response.type() == ResponseType.GET_UPDATE_TASKS) {
            System.out.println("before update");
            try{
                client.updateTaskList();
            } catch (ContestException e) {
                throw new RuntimeException(e);
            }
            System.out.println("after update");
        }
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.GET_UPDATE_TASKS;
    }

    private class ReaderThread implements Runnable {
        @Override
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("Response received " + response);
                    if (isUpdate((Response) response)) {
                        System.out.println("i'm an update" + response);
                        handleUpdate((Response) response);
                    } else {
                        try {
                            qresponses.put((Response) response);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
