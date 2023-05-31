package com.example.contest.domain;

import java.util.Objects;

public class Participant implements Entity<Integer>{
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participant that)) return false;
        return getId() == that.getId() && getAge() == that.getAge() && Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getAge());
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
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
