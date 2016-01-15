package com.getknowledge.platform.modules.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.getknowledge.platform.annotations.Action;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.criteria.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Semaphore;

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
                        for (Task task : tasks) {
                            new Thread(() -> {
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
                                    task.setStackTrace(ExceptionUtils.getStackTrace(e));
                                    taskRepository.update(task);
                                }
                            }).start();
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
        });
        thread.start();
    }

    @Action(name = "taskFilter" , mandatoryFields = {"first" , "max"})
    @Transactional
    public List<Task> taskFilter(HashMap<String,Object> data) throws NotAuthorized {

        if (!isAccessToRead(data,new Task(),userRepository)) {
            throw new NotAuthorized("access denied for read tasks",traceService,TraceLevel.Warning);
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> q = cb.createQuery(Task.class);
        Root<Task> taskRoot = q.from(Task.class);
        q.select(taskRoot);

        Order order = null;
        if (data.containsKey("order")) {
            Path path = null;
            String orderColumn = (String) data.get("order");
            if (orderColumn.equals("id"))
                path = taskRoot.get("id");
            if (orderColumn.equals("taskStatus"))
                path = taskRoot.get("taskStatus");
            if (orderColumn.equals("calendar"))
                path = taskRoot.get("calendar");

            if (path != null) {
                if (data.containsKey("desc")) {
                    order = cb.desc(path);
                } else {
                    order = cb.asc(path);
                }
                q.orderBy(order);
            }
        }

        Predicate equalPredicate = null;
        if (data.containsKey("filter")){
            String filter = (String) data.get("filter");
            TaskStatus taskStatus = TaskStatus.valueOf(filter);
            equalPredicate = cb.equal(taskRoot.get("taskStatus"),taskStatus);
        }

        Predicate betweenPredicate = null;
        if (data.containsKey("startDate") && data.containsKey("endDate")) {

            Date startDate = new Date((Integer)data.get("startDate"));
            Date endDate = new Date((Integer)data.get("endDate"));

            betweenPredicate = cb.between(taskRoot.get("calendar"),startDate,endDate);
        }

        if (betweenPredicate != null || equalPredicate != null) {
            if (betweenPredicate != null && equalPredicate != null)
                q.where(cb.and(betweenPredicate,equalPredicate));
            else if (betweenPredicate == null)
                q.where(equalPredicate);
            else
                q.where(betweenPredicate);
        }


        int first = (int) data.get("first");
        int max   = (int) data.get("max");

        Query query = entityManager.createQuery(q);
        query.setFirstResult(first);
        query.setMaxResults(max);

        List<Task> tasks = query.getResultList();
        return tasks;
    }
}
