package com.getknowledge.platform.modules.trace;

import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import com.getknowledge.platform.modules.user.UserRepository;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("TraceService")
public class TraceService extends AbstractService {

    Logger logger = LoggerFactory.getLogger(TraceService.class);

    @Autowired
    private TraceRepository traceRepository;

    @Transactional
    public void log(String message, TraceLevel traceLevel, boolean isSaveToDB) {
        logException(message,null,traceLevel,isSaveToDB);
    }

    @Transactional
    public void logException(String message, Exception e, TraceLevel traceLevel , boolean isSaveToDB) {
        if (message == null) message = "";
        if (traceLevel == null) traceLevel = TraceLevel.Debug;

        switch (traceLevel) {
            case Debug:
                if (!logger.isDebugEnabled()) break;
                if (e == null) logger.debug(message);
                else logger.debug(message,e);
                break;
            case Event:
                if (!logger.isInfoEnabled()) break;
                if (e == null) logger.info(message);
                else logger.info(message,e);
                break;
            case Warning:
                if (!logger.isWarnEnabled()) break;
                if (e == null) logger.warn(message);
                else logger.warn(message,e);
                break;
            case Error:
                if (!logger.isErrorEnabled()) break;
                if (e == null) logger.error(message);
                else logger.error(message,e);
                break;
            case Critical:
                if (e == null) logger.error(message);
                else logger.error(message,e);
                break;
        }

        if (isSaveToDB) {
            Trace trace = new Trace();
            trace.setMessage(message);
            trace.setTraceLevel(traceLevel);
            if (e != null)
                trace.setStackTrace(ExceptionUtils.getStackTrace(e));
            traceRepository.create(trace);
        }
    }
}
