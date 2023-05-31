package com.example.network;

import com.example.*;
import com.example.enums.AgeGroup;
import com.example.enums.Type;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class ContestClientReflectionWorker implements Runnable, IContestObserver {
    private IContestService server;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public ContestClientReflectionWorker(IContestService server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTaskList() throws ContestException {
        Response response = new Response.Builder().type(ResponseType.GET_UPDATE_TASKS).build();
        System.out.println("Updating tasks list...");
        try {
            sendResponse(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                Object request = input.readObject();
                Response response = handleRequest((Request) request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error " + e);
        }
    }

    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();

    private Response handleRequest(Request request) {
        Response response = null;
        String handlerName = "handle" + (request).type();
        System.out.println("HandlerName " + handlerName);
        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            System.out.println("Method " + handlerName + " invoked");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return response;
    }

    private Response handleLOGIN(Request request) {
        System.out.println("Login request... " + request.type());
        User user = (User) request.data();
        System.out.println("Logging user " + user.toString() + " in");
        try {
            server.login(user, this);
            return okResponse;
        } catch (ContestException e) {
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Response handleLOGOUT(Request request) {
        System.out.println("Logout request... " + request.type());
        User user = (User) request.data();
        System.out.println("Logging user " + user.toString() + " out");
        try {
            server.logout(user, this);
            connected = false;
            return okResponse;
        } catch (ContestException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_ALL_TASKS_EXPERIMENTAL(Request request){
        System.out.println("Getting all the tasks... (experimental)");
        try{
            return new Response.Builder().type(ResponseType.GET_ALL_TASKS_EXPERIMENTAL).data(server.getTasksEnrolled()).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Response handleADD_PARTICIPANT_EXPERIMENTAL(Request request) throws ContestException {
        System.out.println("Adding participant...");
        ParticipantTask pt = (ParticipantTask) request.data();
        try{
            server.register(pt.getName(), pt.getAge(), pt.getType1(), pt.getType2());
            return new Response.Builder().type(ResponseType.ADD_PARTICIPANT_EXPERIMENTAL).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Response handleFILTER(Request request) throws ContestException{
        System.out.println("Filtering...");
        String[] filters = (String[]) request.data();
        try{
            return new Response.Builder().type(ResponseType.FILTER).data(server.getFilteredParticipants(Type.valueOf(filters[0]), AgeGroup.valueOf(filters[1]))).build();
        } catch (ContestException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendResponse(Response response) throws IOException {
        System.out.println("sending response " + response);
        synchronized (output) {
            output.writeObject(response);
            output.flush();
        }
        //output.writeObject(response);
        //output.flush();
    }
}
