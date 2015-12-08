package com.getknowledge.platform.modules.trace;

import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
        logException(message,null,traceLevel);
    }

    public void logException(String message, Exception e, TraceLevel traceLevel) {
        if (message == null) message = "";
        if (traceLevel == null) traceLevel = TraceLevel.Debug;

        switch (traceLevel) {
            case Debug:
                if (e == null) logger.debug(message);
                else logger.debug(message,e);
                break;
            case Event:
                if (e == null) logger.info(message);
                else logger.info(message,e);
                break;
            case Warning:
                if (e == null) logger.warn(message);
                else logger.warn(message,e);
                break;
            case Error:
                if (e == null) logger.error(message);
                else logger.error(message,e);
                break;
            case Critical:
                if (e == null) logger.error(message);
                else logger.error(message,e);
                break;
        }

        Trace trace = new Trace();
        trace.setMessage(message);
        trace.setTraceLevel(traceLevel);
        if (e != null)
            trace.setStackTrace(ExceptionUtils.getStackTrace(e));
        traceRepository.create(trace);
    }
}
