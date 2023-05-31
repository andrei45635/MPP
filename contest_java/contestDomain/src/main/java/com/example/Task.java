package com.example;
import com.example.enums.AgeGroup;
import com.example.enums.Type;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.io.Serializable;
import java.util.Objects;

public class Task implements Entity<Integer>, Serializable {
    private int id;
    Type type;
    AgeGroup ageGroup;

    public Task() {}

    public Task(int id, Type type, AgeGroup ageGroup) {
        this.id = id;
        this.type = type;
        this.ageGroup = ageGroup;
    }

    public Task(Type type, AgeGroup ageGroup){
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
    public String toString() {
        return "Task{" +
                "type=" + type +
                ", ageGroup=" + ageGroup +
                ", id=" + id +
                '}';
    }

    @Override
    @JsonProperty("id")
    public void setId(Integer integer) {
        this.id = integer;
    }

    @Override
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }
}
