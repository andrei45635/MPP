package com.example;

import com.example.enums.Type;

import java.io.Serializable;
import java.util.Objects;

public class ParticipantTask implements Entity<Integer>, Serializable {
    private int id;
    private int participantID;
    private String name;
    private int age;
    private Type type1;
    private Type type2;
    private int task1ID;
    private int task2ID;

    public ParticipantTask () {}

    public ParticipantTask(int id, int participantID, int task1ID, int task2ID) {
        this.id = id;
        this.participantID = participantID;
        this.task1ID = task1ID;
        this.task2ID = task2ID;
    }

    public ParticipantTask(int participantID, int task1ID, int task2ID) {
        this.participantID = participantID;
        this.task1ID = task1ID;
        this.task2ID = task2ID;
    }

    public ParticipantTask(String name, int age, Type type1, Type type2) {
        this.name = name;
        this.age = age;
        this.type1 = type1;
        this.type2 = type2;
    }

    public int getParticipantID() {
        return participantID;
    }

    public void setParticipantID(int participantID) {
        this.participantID = participantID;
    }

    public int getTask1ID() {
        return task1ID;
    }

    public void setTask1ID(int task1ID) {
        this.task1ID = task1ID;
    }

    public int getTask2ID() {
        return task2ID;
    }

    public void setTask2ID(int task2ID) {
        this.task2ID = task2ID;
    }

    public Type getType1() {
        return type1;
    }

    public Type getType2() {
        return type2;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "ParticipantTask{" +
                "participantID=" + participantID +
                ", task1ID=" + task1ID +
                ", task2ID=" + task2ID +
                ", id=" + id +
                '}';
    }

    @Override
    public void setId(Integer integer) {
        this.id = integer;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
