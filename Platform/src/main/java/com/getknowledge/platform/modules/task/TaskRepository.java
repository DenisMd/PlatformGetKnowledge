package com.getknowledge.platform.modules.task;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;
import sun.reflect.generics.repository.AbstractRepository;

@Repository("TaskRepository")
public class TaskRepository extends BaseRepository<Task> {

    @Override
    public void create(Task object) {
        super.create(object);
        TaskService.thread.interrupt();
    }
}
