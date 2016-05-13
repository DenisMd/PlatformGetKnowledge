package com.getknowledge.platform.modules.task;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.enumerations.RepOperations;
import com.getknowledge.platform.modules.task.enumerations.TaskStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Repository("TaskRepository")
public class TaskRepository extends BaseRepository<Task> {

    @Override
    public List<RepOperations> restrictedOperations() {
        List<RepOperations> operations = new ArrayList<>();
        operations.add(RepOperations.Create);
        operations.add(RepOperations.Update);
        operations.add(RepOperations.Remove);
        return operations;
    }

    @Override
    protected Class<Task> getClassEntity() {
        return Task.class;
    }

    @Override
    public void create(Task object) {
        super.create(object);
        TaskService.thread.interrupt();
    }

    public Task createTask(String serviceName, String taskName, String data,Calendar startTime) {
        Task task = new Task();
        task.setServiceName(serviceName);
        task.setTaskName(taskName);
        task.setJsonData(data);
        task.setTaskStatus(TaskStatus.NotStarted);
        task.setStartDate(startTime);
        create(task);
        return task;
    }
}
