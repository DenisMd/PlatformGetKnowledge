package com.getknowledge.platform.modules.task;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.modules.task.enumerations.TaskStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

@Repository("TaskRepository")
public class TaskRepository extends BaseRepository<Task> {

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
