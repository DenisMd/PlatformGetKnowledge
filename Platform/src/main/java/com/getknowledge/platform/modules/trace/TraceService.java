package com.getknowledge.platform.modules.trace;

import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("TraceService")
public class TraceService extends AbstractService {

    Logger logger = LoggerFactory.getLogger(TraceService.class);

    @Autowired
    private TraceRepository traceRepository;

    public void log(String message, TraceLevel traceLevel) {
        if (message == null) message = "";
        if (traceLevel == null) traceLevel = TraceLevel.Debug;

        switch (traceLevel) {
            case Debug:
                logger.debug(message);
                break;
            case Event:
                logger.info(message);
                break;
            case Warning:
                logger.warn(message);
                break;
            case Error:
                logger.error(message);
                break;
            case Critical:
                logger.error(message);
                break;
        }

        Trace trace = new Trace();
        trace.setMessage(message);
        trace.setTraceLevel(traceLevel);
        traceRepository.create(trace);
    }

    public void logException(String message, Exception e, TraceLevel traceLevel) {
        if (message == null) message = "";
        if (traceLevel == null) traceLevel = TraceLevel.Debug;

        switch (traceLevel) {
            case Debug:
                logger.debug(message,e);
                break;
            case Event:
                logger.info(message,e);
                break;
            case Warning:
                logger.warn(message,e);
                break;
            case Error:
                logger.error(message,e);
                break;
            case Critical:
                logger.error(message,e);
                break;
        }

        Trace trace = new Trace();
        trace.setMessage(message);
        trace.setTraceLevel(traceLevel);
        traceRepository.create(trace);


    }
}
