package com.example.services.rest;

import com.example.Task;
import com.example.repo.tasks.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/contest/tasks")
public class ContestTaskController {
    private static final String template = "Hello, %s!";
    @Autowired
    private TaskRepository taskRepository;

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", defaultValue = "Fesa Robert") String name){
        return String.format(template, name);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Task> getAllTasks(){
        System.out.println("Getting all the tasks RESTfully");
        return taskRepository.getAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public Task save(@RequestBody Task task) throws IOException {
        System.out.println("Adding the task " + task.toString());
        return taskRepository.save(task);
    }

    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable int id){
        System.out.println("Deleting task with the id... " + id);
        try{
            taskRepository.deleteById(id);
            return new ResponseEntity<Task>(HttpStatus.OK);
        } catch (ServiceException e) {
            System.out.println("Exception when deleting the task in the Task Controller");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value="/{id}", method = RequestMethod.PUT)
    public Task update(@RequestBody Task task, @PathVariable int id) throws IOException {
        System.out.println("Updating the task " + task + "... " + id);
        task.setId(id);
        taskRepository.update(task);
        return task;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> findById(@PathVariable int id){
        System.out.println("Finding the task with the id " + id);
        Task task = taskRepository.findById(id);
        if(task == null){
            return new ResponseEntity<>("Task not found", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<Task>(task, HttpStatus.OK);
        }
    }

    @RequestMapping("/{id}/age-group")
    public String ageGroup(@PathVariable int id){
        Task task = taskRepository.findById(id);
        System.out.println("Resulted task... " + task);
        return task.getAgeGroup().toString();
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String taskError(ServiceException e) {
        return e.getMessage();
    }
}
