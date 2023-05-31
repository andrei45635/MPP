package com.example.dto;
import com.example.enums.Type;
import com.example.enums.AgeGroup;

import java.io.Serializable;
import java.util.Objects;

public class TaskDTO implements Serializable {
    private int TaskID;
    private Type Type;
    private AgeGroup AgeGroup;
    private int Enrolled;

    public TaskDTO(int taskID, Type type, AgeGroup ageGroup, int enrolled) {
        TaskID = taskID;
        Type = type;
        AgeGroup = ageGroup;
        Enrolled = enrolled;
    }

    public int getTaskID() {
        return TaskID;
    }

    public Type getType() {
        return Type;
    }

    public AgeGroup getAgeGroup() {
        return AgeGroup;
    }

    public int getEnrolled() {
        return Enrolled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskDTO taskDTO = (TaskDTO) o;
        return TaskID == taskDTO.TaskID && Enrolled == taskDTO.Enrolled && Type == taskDTO.Type && AgeGroup == taskDTO.AgeGroup;
    }

    @Override
    public int hashCode() {
        return Objects.hash(TaskID, Type, AgeGroup, Enrolled);
    }
}
