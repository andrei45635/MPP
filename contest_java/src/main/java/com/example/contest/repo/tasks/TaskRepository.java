package com.example.contest.repo.tasks;

import com.example.contest.domain.Task;
import com.example.contest.domain.enums.AgeGroup;
import com.example.contest.domain.enums.Type;
import com.example.contest.repo.IRepository;

import java.util.List;

public interface TaskRepository extends IRepository<Integer, Task> {
    List<Task> findTaskByAge(AgeGroup age);
    List<Task> findTaskByType(Type type);
    int getTasksByAgeType(AgeGroup age, Type type);
}
