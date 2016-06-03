package com.getknowledge.platform.modules.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.CrudService;
import com.getknowledge.platform.modules.task.enumerations.TaskStatus;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import com.getknowledge.platform.modules.user.UserRepository;
import com.getknowledge.platform.utils.ModuleLocator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import javax.persistence.TemporalType;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Service("TaskService")
public class TaskService extends AbstractService {

    public static Thread thread;

    @Autowired
    TraceService traceService;

    @Autowired
    private CrudService crudService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModuleLocator moduleLocator;

    public void startup() {
        thread = new Thread(() -> {
            while (true) {
                try {
                    List<Task> tasks = entityManager.createQuery("select task from Task task where task.startDate < :today " +
                            "and task.taskStatus = :status")
                            .setParameter("today", Calendar.getInstance(), TemporalType.TIMESTAMP)
                            .setParameter("status", TaskStatus.NotStarted)
                            .getResultList();

                    //Выполняем задачи
                    if (!tasks.isEmpty()) {
                        ExecutorService executorService = Executors.newCachedThreadPool();
                        for (Task task : tasks) {
                            executorService.execute(() -> {
                                try {
                                    traceService.log("Start task " + task.toString() , TraceLevel.Event,false);
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
                                            crudService.merge(task,taskRepository);
                                            traceService.log("Task " + task.toString() + " complete", TraceLevel.Event,false);
                                            return;
                                        }
                                    }
                                } catch (Exception e) {
                                    traceService.logException("Exception for task : " + task.toString(), e, TraceLevel.Error,false);
                                    task.setTaskStatus(TaskStatus.Failed);
                                    task.setStackTrace(ExceptionUtils.getStackTrace(e));
                                    crudService.merge(task,taskRepository);
                                    return;
                                }

                                traceService.log("Task " + task.toString() + " not found", TraceLevel.Error,true);
                            });
                        }
                        executorService.awaitTermination(2, TimeUnit.MINUTES);
                    }

                    List<Calendar> calendars = entityManager.createQuery("select min(task.startDate) from Task task where task.taskStatus = :status")
                            .setParameter("status", TaskStatus.NotStarted).getResultList();

                    //Засыпаем на определенное время или ждем сигнала прирывания(вызывается при создание таски)
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
                    traceService.logException("Exception for task service", e, TraceLevel.Error,true);
                }
            }
        });
        thread.start();
    }
}
