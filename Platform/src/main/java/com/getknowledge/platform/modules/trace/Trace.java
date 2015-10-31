package com.getknowledge.platform.modules.trace;

import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "trace")
@ModuleInfo(repositoryName = "TraceRepository" , serviceName = "TraceService")
public class Trace extends AbstractEntity {

    @Column(length = 500)
    private String message;

    @Column(name = "trace_level")
    @Enumerated(EnumType.STRING)
    private TraceLevel traceLevel;

    private Calendar calendar = Calendar.getInstance();

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
        this.message = message;
    }

    public Calendar getCalendar() {
        return calendar;
    }


    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
