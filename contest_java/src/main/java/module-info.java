module com.example.contest {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;

    opens com.example.contest to javafx.fxml;
    exports com.example.contest;
    exports com.example.contest.controller;
    exports com.example.contest.service;
    exports com.example.contest.repo;
    exports com.example.contest.repo.tasks;
    exports com.example.contest.repo.users;
    exports com.example.contest.repo.participantstasks;
    exports com.example.contest.repo.participants;
    exports com.example.contest.domain;
    exports com.example.contest.domain.enums;
    opens com.example.contest.dto to javafx.base;
    opens com.example.contest.controller to javafx.fxml;
}