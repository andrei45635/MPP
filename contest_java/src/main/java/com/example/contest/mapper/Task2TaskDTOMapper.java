package com.example.contest.mapper;

import com.example.contest.domain.Task;
import com.example.contest.dto.TaskDTO;
import com.example.contest.repo.tasks.TaskRepository;

import java.util.ArrayList;
import java.util.List;

public class Task2TaskDTOMapper {
    private TaskRepository taskRepository;

    public Task2TaskDTOMapper(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskDTO convert(Task task){
        int enrolled = taskRepository.getTasksByAgeType(task.getAgeGroup(), task.getType());
        return new TaskDTO(task.getId(), task.getType(), task.getAgeGroup(), enrolled);
    }

    public List<TaskDTO> convert (List<Task> tasks){
        List<TaskDTO> tasksDTO = new ArrayList<>();
        for(Task task: tasks){
            tasksDTO.add(convert(task));
        }
        return tasksDTO;
    }
}
