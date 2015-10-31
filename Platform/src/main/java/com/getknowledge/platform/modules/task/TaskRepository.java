package com.getknowledge.platform.modules.task;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;
import sun.reflect.generics.repository.AbstractRepository;

@Repository("TaskRepository")
public class TaskRepository extends BaseRepository<Task> {
}
