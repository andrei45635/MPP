package com.example.repo.tasks;

import com.example.Task;
import com.example.enums.AgeGroup;
import com.example.enums.Type;
import com.example.repo.IRepository;

import java.util.List;

public interface TaskRepository extends IRepository<Integer, Task> {
    List<Task> findTaskByAge(AgeGroup age);
    List<Task> findTaskByType(Type type);
    Task findById(int id);
    int getTasksByAgeType(AgeGroup age, Type type);
    boolean deleteById(int id);
}
