package com.getknowledge.platform.modules.task;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

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
}
