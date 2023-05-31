package com.example.utils;

import com.example.IContestService;
import com.example.network.ContestClientReflectionWorker;
import com.example.protobuff.ContestProtoWorker;

import java.net.Socket;

public class ContestProtoConcurrentServer extends AbsConcurrentServer {
    private IContestService contestServer;

    public ContestProtoConcurrentServer(int port, IContestService contestServer) {
        super(port);
        this.contestServer = contestServer;
        System.out.println("Contest- ContestProtoConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ContestProtoWorker worker = new ContestProtoWorker(contestServer, client);
        return new Thread(worker);
    }

    @Override
    public void stop(){
        System.out.println("Stopping services ...");
    }
}
