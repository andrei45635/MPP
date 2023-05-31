package com.example.protobuff;

import com.example.ContestException;
import com.example.IContestObserver;
import com.example.IContestService;
import com.example.User;

import java.io.*;
import java.net.Socket;

public class ContestProtoWorker implements Runnable, IContestObserver {
    private IContestService server;
    private Socket connection;
    private InputStream input;
    private OutputStream output;
    private volatile boolean connected;

    public ContestProtoWorker(IContestService server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = connection.getOutputStream();
            output.flush();
            input = connection.getInputStream();
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTaskList() throws ContestException {
        System.out.println("Updating tasks list...");
        try {
            sendResponse(ProtoUtils.createUpdateResponse());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void run() {
        while (connected) {
            try {
                System.out.println("Awaiting requests...");
                ContestProto.ContestRequest request = ContestProto.ContestRequest.parseDelimitedFrom(input);
                System.out.println("Request received " + request);
                ContestProto.ContestResponse response = handleRequest(request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(1000);
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

    private void sendResponse(ContestProto.ContestResponse response) throws IOException {
        System.out.println("Sending response " + response);
        synchronized (output) {
            response.writeDelimitedTo(output);
            output.flush();
        }
    }

    private ContestProto.ContestResponse handleRequest(ContestProto.ContestRequest request) {
        ContestProto.ContestResponse response = null;
        switch (request.getReq()) {
            case Login:
                System.out.println("Login request...");
                User user = ProtoUtils.getUser(request);
                try {
                    server.login(user, this);
                    return ProtoUtils.createOKResponse();
                } catch (IOException | ContestException e) {
                    connected = false;
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            case Logout:
                System.out.println("Logout request...");
                User user1 = ProtoUtils.getUser(request);
                try {
                    server.logout(user1, this);
                    return ProtoUtils.createOKResponse();
                } catch (ContestException e) {
                    connected = false;
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            case ADD_PARTICIPANT_EXPERIMENTAL:
                System.out.println("Registering participant request...");
                var pt = ProtoUtils.getPT(request);
                try {
                    server.register(pt.getName(), pt.getAge(), pt.getType1(), pt.getType2());
                    return ProtoUtils.createRegisterResponse();
                } catch (IOException | ContestException e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            case GET_ALL_TASKS_EXPERIMENTAL:
                System.out.println("Getting all the tasks request...");
                try {
                    return ProtoUtils.createGetTasksEnrolledResponse(ProtoUtils.getEnrolledTasksForResponse(server.getTasksEnrolled()));
                } catch (ContestException e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
            case FILTER:
                System.out.println("Filtering participants request...");
                var filters = ProtoUtils.getFilters(request);
                try {
                    var pdt = ProtoUtils.getFilteredPartiticipants(server.getFilteredParticipants(com.example.enums.Type.valueOf(filters[0]), com.example.enums.AgeGroup.valueOf(filters[1])));
                    return ProtoUtils.createGetFilteredParticipantsResponse(ProtoUtils.getFilteredPartsDTOMapper(pdt));
                } catch (ContestException | IllegalArgumentException e) {
                    return ProtoUtils.createErrorResponse(e.getMessage());
                }
        }
        return response;
    }
}
