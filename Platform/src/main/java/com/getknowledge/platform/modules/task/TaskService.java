package com.getknowledge.platform.modules.task;

import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.modules.task.enumerations.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.TemporalType;
import java.util.Calendar;

@Service("TaskService")
public class TaskService extends AbstractService {

    @Autowired
    TaskRepository taskRepository;

    public void startup() {
        Task task = new Task();
        task.setStartDate(Calendar.getInstance());
        task.setServiceName("Wiii");
        task.setTaskName("Defaukt");
        task.setTaskStatus(TaskStatus.NotStarted);
        taskRepository.create(task);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000);
                        int size = entityManager.createQuery("select task from Task task where task.startDate < :today")
                                .setParameter("today" , Calendar.getInstance(), TemporalType.TIMESTAMP).getResultList().size();
                        System.err.println("Tasks : " + size);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
