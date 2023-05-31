package com.example.client;

import com.example.Task;
import com.example.services.rest.ServiceException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;

public class TaskClient {
    public static final String URL = "http://localhost:8080/contest/tasks";
    private RestTemplate restTemplate = new RestTemplate();

    private <T> T execute(Callable<T> callable){
        try{
            return callable.call();
        } catch (ResourceAccessException | HttpClientErrorException e) { // server down, resource exception
            throw new ServiceException(e);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public Task[] getAll(){
        return execute(() -> restTemplate.getForObject(URL, Task[].class));
    }

    public Task getById(int id){
        return execute(() -> restTemplate.getForObject(String.format("%s/%d", URL, id), Task.class));
    }


    public Task create(Task task){
        return execute(() -> restTemplate.postForObject(URL, task, Task.class));
    }

    public void update(Task task){
        execute(() -> {
            restTemplate.put(String.format("%s/%d", URL, task.getId()), task);
            return null;
        });
    }

    public void updateById(int id, Task task){
        execute(() -> {
            restTemplate.put(String.format("%s/%d", URL, id), task);
            return null;
        });
    }

    public void delete(int id){
        execute(() -> {
            restTemplate.delete(String.format("%s/%d", URL, id));
            return null;
        });
    }
}
