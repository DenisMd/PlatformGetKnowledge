package com.getknowledge.platform.modules.trace;

import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "sys_trace")
@ModuleInfo(repositoryName = "TraceRepository" , serviceName = "TraceService")
public class Trace extends AbstractEntity {

    @Column(length = 500)
    private String message;

    @Column(name = "trace_level" , nullable = false)
    @Enumerated(EnumType.STRING)
    private TraceLevel traceLevel;

    @Column(columnDefinition = "Text" , name = "stack_trace")
    private String stackTrace;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar calendar = Calendar.getInstance();

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public TraceLevel getTraceLevel() {
        return traceLevel;
    }

    public void setTraceLevel(TraceLevel traceLevel) {
        this.traceLevel = traceLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if (message.length() > 500) {
            this.message = message.substring(0,500);
        } else {
            this.message = message;
        }
    }

    public Calendar getCalendar() {
        return calendar;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
