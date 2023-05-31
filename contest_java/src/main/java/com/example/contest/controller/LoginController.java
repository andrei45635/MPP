package com.example.contest.controller;

import com.example.contest.domain.User;
import com.example.contest.service.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField usernameLoginTF;
    @FXML
    private PasswordField passwordLoginTF;
    private Service service;

    public void setService(Service service){
        this.service = service;
    }

    @FXML
    private void onClickLogin(ActionEvent actionEvent) throws IOException {
        if(!service.checkUserExists(usernameLoginTF.getText(), passwordLoginTF.getText())){
            errorLabel.setText("Invalid credentials!");
            return;
        }

        User loggedInUser = service.findLoggedInUser(usernameLoginTF.getText(), passwordLoginTF.getText());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/contest/user-controller.fxml"));
        Parent root = loader.load();
        UserController userViewController = loader.getController();
        userViewController.setLoggedInUser(loggedInUser);
        userViewController.setService(service);
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Hello!");
        stage.show();
        Stage thisStage = (Stage) loginButton.getScene().getWindow();
        thisStage.close();
    }
}
