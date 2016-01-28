package com.getknowledge.platform.modules.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.repositories.FilterQuery;
import com.getknowledge.platform.base.repositories.enumerations.OrderRoute;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.exceptions.ModuleNotFound;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.modules.task.enumerations.TaskStatus;
import com.getknowledge.platform.modules.trace.Trace;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.modules.user.UserRepository;
import com.getknowledge.platform.utils.ModuleLocator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.xml.SAXErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.criteria.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Service("TaskService")
public class TaskService extends AbstractService {

    @Autowired
    private UserRepository userRepository;

    public static Semaphore semaphore;

    public static Thread thread;
    public static boolean isStart = true;

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
                                    traceService.log("Start task " + task.toString() , TraceLevel.Event);
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
                                            traceService.log("Task " + task.toString() + " complete", TraceLevel.Event);
                                            return;
                                        }
                                    }
                                } catch (Exception e) {
                                    traceService.logException("Exception for task : " + task.toString(), e, TraceLevel.Warning);
                                    task.setTaskStatus(TaskStatus.Failed);
                                    task.setStackTrace(ExceptionUtils.getStackTrace(e));
                                    taskRepository.update(task);
                                    return;
                                }

                                traceService.log("Task " + task.toString() + " not found", TraceLevel.Error);
                            });
                        }
                        executorService.awaitTermination(2, TimeUnit.MINUTES);
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
        });
        thread.start();
    }

    @Action(name = "taskFilter" , mandatoryFields = {"first" , "max"})
    @Transactional
    public List<Task> taskFilter(HashMap<String,Object> data) throws NotAuthorized {

        if (!isAccessToRead(data,new Task(),userRepository)) {
            throw new NotAuthorized("access denied for read tasks",traceService,TraceLevel.Warning);
        }

        FilterQuery<Task> filter = taskRepository.initFilter();

        if (data.containsKey("order") && !((String)data.get("order")).isEmpty()) {
            OrderRoute orderRoute = OrderRoute.Asc;
            if (data.containsKey("desc")) {
                orderRoute = OrderRoute.Desc;
            }

            filter.setOrder((String) data.get("order") , orderRoute);
        }

        Predicate equalPredicate = null;
        if (data.containsKey("filter")){
            String filterValue = (String) data.get("filter");
            filter.equal("taskStatus" , TaskStatus.valueOf(filterValue));
        }

        Predicate betweenPredicate = null;
        if (data.containsKey("startDate") && data.containsKey("endDate")) {

            Date startDate = new Date((Integer)data.get("startDate"));
            Date endDate = new Date((Integer)data.get("endDate"));

            filter.betweenDates("calendar" , startDate, endDate);
        }


        int first = (int) data.get("first");
        int max   = (int) data.get("max");

        Query query = filter.getQuery(first , max);
        query.setFirstResult(first);
        query.setMaxResults(max);

        List<Task> tasks = query.getResultList();
        return tasks;
    }
}
