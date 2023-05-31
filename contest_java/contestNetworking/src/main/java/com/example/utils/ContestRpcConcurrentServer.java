package com.example.utils;

import com.example.IContestService;
import com.example.network.ContestClientReflectionWorker;

import java.net.Socket;

public class ContestRpcConcurrentServer extends AbsConcurrentServer{
    private IContestService contestServer;
    public ContestRpcConcurrentServer(int port, IContestService contestServer) {
        super(port);
        this.contestServer = contestServer;
        System.out.println("Contest- ContestRpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ContestClientReflectionWorker worker = new ContestClientReflectionWorker(contestServer, client);

        Thread tw=new Thread(worker);
        return tw;
    }

    @Override
    public void stop(){
        System.out.println("Stopping services ...");
    }
}
