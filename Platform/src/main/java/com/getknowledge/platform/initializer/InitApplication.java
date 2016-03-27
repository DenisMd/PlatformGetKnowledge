package com.getknowledge.platform.initializer;

import com.getknowledge.platform.modules.task.TaskService;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Configurable
public class InitApplication {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TraceService traceService;

    @Autowired
    private ApplicationContext context;

    private Logger logger = LoggerFactory.getLogger(InitApplication.class);

    private Map<String, Object> services = new HashMap<>();

    public Map<String, Object> getServices() {
        return services;
    }

    @Transactional
    public void init () {
        traceService.log("Application start", TraceLevel.Event);

        services = context.getBeansWithAnnotation(Service.class);

        for (Map.Entry<String,Object> entry : services.entrySet()) {
            logger.info("Service started " + entry.getKey());
        }

        taskService.startup();
    }
}
