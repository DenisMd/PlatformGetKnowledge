package com.getknowledge.platform.modules.task;

import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.task.enumerations.TaskStatus;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "task")
@ModuleInfo(repositoryName = "TaskRepository" , serviceName = "TaskService")
public class Task  extends AbstractEntity{

    @Column(name = "name")
    private String taskName;

    @Column(columnDefinition = "Text",name = "json_data")
    private String jsonData;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status")
    private TaskStatus taskStatus = TaskStatus.NotStarted;

    @Column(name = "start_date")
    private Calendar startDate;

    @Column(name = "service_name")
    private String serviceName;

    @Column(columnDefinition = "Text" , name = "stack_trace")
    private String stackTrace;

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
