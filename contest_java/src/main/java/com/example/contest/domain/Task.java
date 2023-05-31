package com.example.contest.domain;

import com.example.contest.domain.enums.Type;
import com.example.contest.domain.enums.AgeGroup;

import java.util.Objects;

public class Task implements Entity<Integer> {
    private int id;
    Type type;
    AgeGroup ageGroup;

    public Task(int id, Type type, AgeGroup ageGroup) {
        this.id = id;
        this.type = type;
        this.ageGroup = ageGroup;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(AgeGroup ageGroup) {
        this.ageGroup = ageGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return getId() == task.getId() && getType() == task.getType() && getAgeGroup() == task.getAgeGroup();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getType(), getAgeGroup());
    }

    @Override
    public String toString() {
        return "Task{" +
                "type=" + type +
                ", ageGroup=" + ageGroup +
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
