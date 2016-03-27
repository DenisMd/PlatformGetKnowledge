package com.getknowledge.platform.modules.task;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("TaskRepository")
public class TaskRepository extends BaseRepository<Task> {

    @Override
    protected Class<Task> getClassEntity() {
        return Task.class;
    }

    @Override
    @Transactional
    public void create(Task object) {
        super.create(object);
        TaskService.thread.interrupt();
    }
}
