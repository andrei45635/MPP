package com.example.start;

import com.example.Task;
import com.example.client.TaskClient;
import com.example.enums.AgeGroup;
import com.example.enums.Type;
import com.example.services.rest.ServiceException;
import org.springframework.web.client.RestClientException;

public class StartRestClient {
    private final static TaskClient taskClient = new TaskClient();

    public static void main(String[] args) {
        int id = 203;
        Task testTask = new Task(id, Type.POETRY, AgeGroup.YOUNG);
        try {
            show(() -> System.out.println(taskClient.create(testTask)));
            show(() -> {
                Task[] res = taskClient.getAll();
                for (Task t : res) {
                    System.out.println(t.toString());
                }
            });
        } catch (RestClientException ex) {
            System.out.println("Exception ... " + ex.getMessage());
        }
        show(() -> System.out.println(taskClient.getById(id)));
        Task updateTask = new Task(id, Type.TREASURE, AgeGroup.TEEN);
        taskClient.update(updateTask);
        taskClient.delete(id);
    }

    private static void show(Runnable task) {
        try {
            task.run();
        } catch (ServiceException e) {
            System.out.println("Service exception" + e);
        }
    }
}
