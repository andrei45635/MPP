package com.example;

import com.example.controller.LoginController;
import com.example.controller.UserController;
import com.example.network.ContestServiceRpcProxy;
import com.example.protobuff.ContestProtoProxy;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Properties;

public class StartProtoClientFX extends Application {
    private Stage primaryStage;
    private static int defaultContestPort = 55588;
    private static String defaultServer = "localhost";

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("In start");
        Properties clientProps = new Properties();
        //ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:spring-client.xml");
        //IContestService server = (IContestService) factory.getBean("contestService");
        try {
            clientProps.load(StartRpcClientFX.class.getResourceAsStream("/contestclient.properties"));
            System.out.println("Client properties set.");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find contestclient.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("contest.server.host", defaultServer);
        int serverPort = defaultContestPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("contest.server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultContestPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        IContestService server = new ContestProtoProxy(serverIP, serverPort);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/hello-view.fxml"));
        Parent root = loader.load();

        LoginController ctrl = loader.getController();
        ctrl.setServer(server);

        FXMLLoader cloader = new FXMLLoader(getClass().getClassLoader().getResource("views/user-controller.fxml"));
        Parent croot = cloader.load();

        UserController userController = cloader.getController();
        //userController.setServer(server);

        ctrl.setUserController(userController);
        ctrl.setParent(croot);

        primaryStage.setTitle("Contest Viewer");
        primaryStage.setScene(new Scene(root, 500, 300));
        primaryStage.show();
    }
}
