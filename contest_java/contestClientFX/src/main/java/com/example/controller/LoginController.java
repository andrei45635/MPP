package com.example.controller;

import com.example.ContestException;
import com.example.IContestService;
import com.example.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class LoginController {
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField usernameLoginTF;
    @FXML
    private PasswordField passwordLoginTF;
    private IContestService server;
    private UserController userController;
    Parent mainUserParent;

    public void setServer(IContestService server) {
        this.server = server;
    }

    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    public void setParent(Parent p) {
        mainUserParent = p;
    }

    @FXML
    private void onClickLogin(ActionEvent actionEvent) throws IOException, ContestException {
        User loggedInUser = new User(new Random().nextInt(1000) + 300, usernameLoginTF.getText(), passwordLoginTF.getText());
        System.out.println(loggedInUser);
        try {
            server.login(loggedInUser, userController);
            userController.setServer(server);
            Stage stage = new Stage();
            stage.setScene(new Scene(mainUserParent, 600, 400));
            stage.setTitle("Hello!");
            stage.show();
            userController.setLoggedInUser(loggedInUser);
            Stage thisStage = (Stage) loginButton.getScene().getWindow();
            thisStage.close();
        } catch (ContestException cx) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Contest Viewer");
            alert.setHeaderText("Authentication failure");
            alert.setContentText("Wrong username or password");
            alert.showAndWait();
        }
    }
}
