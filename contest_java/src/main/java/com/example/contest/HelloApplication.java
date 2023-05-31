package com.example.contest;

import com.example.contest.controller.LoginController;
import com.example.contest.repo.participants.ParticipantsDBRepository;
import com.example.contest.repo.participantstasks.ParticipantsTasksDBRepository;
import com.example.contest.repo.tasks.TaskDBRepository;
import com.example.contest.repo.users.UserDBRepository;
import com.example.contest.service.Service;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Properties props=new Properties();
        try {
            props.load(new FileReader("bd.config"));
        } catch (
                IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 425, 300);
        stage.setTitle("Hello!");
        LoginController loginController = fxmlLoader.getController();
        loginController.setService(new Service(new UserDBRepository(props),new TaskDBRepository(props), new ParticipantsDBRepository(props), new ParticipantsTasksDBRepository(props)));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}