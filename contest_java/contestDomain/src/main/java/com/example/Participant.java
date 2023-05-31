package com.example;

import java.io.Serializable;
import java.util.Objects;

public class Participant implements Entity<Integer>, Serializable {
    private int id;
    private String name;
    private int age;

    public Participant(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "name='" + name + '\'' +
                ", age=" + age +
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
