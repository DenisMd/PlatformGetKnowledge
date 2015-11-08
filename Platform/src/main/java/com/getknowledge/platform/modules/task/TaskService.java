package com.getknowledge.platform.modules.task;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.exceptions.ModuleNotFound;
import com.getknowledge.platform.modules.task.enumerations.TaskStatus;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.utils.ModuleLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import javax.persistence.TemporalType;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

@Service("TaskService")
public class TaskService extends AbstractService {

    public static Semaphore semaphore;

    public static Thread thread;

    static {
        semaphore = new Semaphore(1);
    }

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TraceService traceService;

    @Autowired
    private ModuleLocator moduleLocator;

    public void startup() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        List<Task> tasks = entityManager.createQuery("select task from Task task where task.startDate < :today " +
                                "and task.taskStatus = :status")
                                .setParameter("today", Calendar.getInstance(), TemporalType.TIMESTAMP)
                                .setParameter("status", TaskStatus.NotStarted)
                                .getResultList();

                        //Выполняем задачи
                        if (!tasks.isEmpty()) {
                            for (Task task : tasks) {
                                try {
                                    AbstractService abstractService = moduleLocator.findService(task.getServiceName());
                                    for (Method method : abstractService.getClass().getMethods()) {
                                        com.getknowledge.platform.annotations.Task taskAnnotation = AnnotationUtils.findAnnotation(method, com.getknowledge.platform.annotations.Task.class);
                                        if (taskAnnotation == null) {
                                            continue;
                                        }

                                        if (taskAnnotation.name().equals(task.getTaskName())) {
                                            TypeReference<HashMap<String, Object>> typeRef
                                                    = new TypeReference<HashMap<String, Object>>() {
                                            };

                                            HashMap<String, Object> data = objectMapper.readValue(task.getJsonData(), typeRef);
                                            method.invoke(abstractService, data);
                                            task.setTaskStatus(TaskStatus.Complete);
                                            taskRepository.update(task);
                                        }
                                    }
                                } catch (Exception e) {
                                    traceService.logException("Exception for task : " + task.getTaskName(), e, TraceLevel.Warning);
                                    task.setTaskStatus(TaskStatus.Failed);
                                    taskRepository.update(task);
                                }
                            }
                        }

                        List<Calendar> calendars = entityManager.createQuery("select min(task.startDate) from Task task where task.taskStatus = :status")
                                .setParameter("status", TaskStatus.NotStarted).getResultList();

                        if (!calendars.isEmpty()) {
                            if (calendars.get(0) != null) {
                                long delta = calendars.get(0).getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                                Thread.sleep(delta);
                            } else {
                                synchronized (this) {
                                    wait();
                                }
                            }
                        }



                    } catch (InterruptedException e) {
                        continue;
                    } catch (Exception e) {
                        traceService.logException("Exception for task service", e, TraceLevel.Warning);
                    }
                }
            }
        });
        thread.start();
    }

}
