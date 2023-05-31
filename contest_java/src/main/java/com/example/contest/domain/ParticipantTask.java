package com.example.contest.domain;

import java.util.Objects;

public class ParticipantTask implements Entity<Integer>{
    private int id;
    private int participantID;
    private int task1ID;
    private int task2ID;

    public ParticipantTask(int participantID, int task1ID, int task2ID) {
        this.participantID = participantID;
        this.task1ID = task1ID;
        this.task2ID = task2ID;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipantTask that)) return false;
        return getId() == that.getId() && getParticipantID() == that.getParticipantID() && getTask1ID() == that.getTask1ID() && getTask2ID() == that.getTask2ID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getParticipantID(), getTask1ID(), getTask2ID());
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
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
