package com.example.contest.controller;

import com.example.contest.domain.Participant;
import com.example.contest.domain.User;
import com.example.contest.domain.enums.AgeGroup;
import com.example.contest.domain.enums.Type;
import com.example.contest.dto.ParticipantDTO;
import com.example.contest.dto.TaskDTO;
import com.example.contest.mapper.Participant2ParticipantDTOMapper;
import com.example.contest.mapper.Task2TaskDTOMapper;
import com.example.contest.repo.tasks.TaskDBRepository;
import com.example.contest.service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class UserController {
    @FXML
    private Label errorAge;
    @FXML
    private TextField registrationNameTF;
    @FXML
    private TextField ageRegistrationTF;
    @FXML
    private ComboBox<Type> task1ComboBox;
    @FXML
    private ComboBox<Type> task2ComboBox;

    public Button registerButton;
    @FXML
    private ComboBox<Type> taskComboBox;
    @FXML
    private ComboBox<AgeGroup> ageGroupComboBox;
    @FXML
    private TableView<ParticipantDTO> participantsTableView;
    @FXML
    private TableColumn<ParticipantDTO, String> nameParticipantColumn;
    @FXML
    private TableColumn<ParticipantDTO, Integer> ageParticipantColumn;
    @FXML
    private Button logoutButton;
    @FXML
    private TableView<TaskDTO> tableViewTasks;
    @FXML
    private TableColumn<TaskDTO, Integer> taskIDColumn;
    @FXML
    private TableColumn<TaskDTO, Type> typeColumn;
    @FXML
    private TableColumn<TaskDTO, AgeGroup> ageGroupColumn;
    @FXML
    private TableColumn<TaskDTO, Integer> enrolledColumn;

    private final ObservableList<TaskDTO> tasksModel = FXCollections.observableArrayList();
    private final ObservableList<ParticipantDTO> participantsModel = FXCollections.observableArrayList();

    public Properties readProps() {
        Properties props = new Properties();
        try {
            props.load(new FileReader("bd.config"));
        } catch (
                IOException e) {
            System.out.println("Cannot find bd.config " + e);
        }
        return props;
    }

    private final Task2TaskDTOMapper task2TaskDTOMapper = new Task2TaskDTOMapper(new TaskDBRepository(readProps()));
    private final Participant2ParticipantDTOMapper participant2ParticipantDTOMapper = new Participant2ParticipantDTOMapper();

    private Service service;
    private User loggedInUser;

    public UserController() throws IOException {
    }

    public void setService(Service service) throws IOException {
        this.service = service;
        ageGroupComboBox.setItems(FXCollections.observableArrayList(AgeGroup.YOUNG, AgeGroup.PRETEEN, AgeGroup.TEEN));
        taskComboBox.setItems(FXCollections.observableArrayList(Type.PAINTING, Type.POETRY, Type.TREASURE));
        task1ComboBox.setItems(FXCollections.observableArrayList(Type.PAINTING, Type.POETRY, Type.TREASURE));
        task2ComboBox.setItems(FXCollections.observableArrayList(Type.PAINTING, Type.POETRY, Type.TREASURE));
        initModel();
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    @FXML
    private void onClickLogout(ActionEvent actionEvent) throws IOException {
        this.loggedInUser = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/contest/hello-view.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();
        loginController.setService(service);
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 425, 350));
        stage.setTitle("Hello!");
        stage.show();
        Stage thisStage = (Stage) logoutButton.getScene().getWindow();
        thisStage.close();
    }

    @FXML
    public void initialize() {
        taskIDColumn.setCellValueFactory(new PropertyValueFactory<>("TaskID"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("Type"));
        ageGroupColumn.setCellValueFactory(new PropertyValueFactory<>("AgeGroup"));
        enrolledColumn.setCellValueFactory(new PropertyValueFactory<>("Enrolled"));

        tableViewTasks.setItems(tasksModel);

        nameParticipantColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageParticipantColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        participantsTableView.setItems(participantsModel);
    }

    public void initModel() throws IOException {
        tasksModel.setAll(task2TaskDTOMapper.convert(service.getAllTasks()));
    }

    @FXML
    private void onClickFilter(ActionEvent actionEvent) throws IOException {
        Type selectedType = taskComboBox.getValue();
        AgeGroup selectedAgeGroup = ageGroupComboBox.getValue();
        System.out.println(service.getFilteredParticipants(selectedType, selectedAgeGroup).size());
        participantsModel.setAll(participant2ParticipantDTOMapper.convert(service.getFilteredParticipants(selectedType, selectedAgeGroup)));
    }

    @FXML
    private void onClickRegister(ActionEvent actionEvent) throws IOException {
        String inputName = registrationNameTF.getText();
        int age = Integer.parseInt(ageRegistrationTF.getText());
        if(age < 6 || age > 15){
            errorAge.setText("Invalid age!");
            return;
        }
        Participant participant = new Participant(service.getParticipantSize() + 1, inputName, age);
        Type type1 = task1ComboBox.getValue();
        Type type2 = task2ComboBox.getValue();
        service.register(inputName, age, type1, type2);
        initModel();
    }
}
