package com.getknowledge.platform.modules.trace;

import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.modules.user.UserRepository;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.criteria.*;
import java.util.*;

@Service("TraceService")
public class TraceService extends AbstractService {

    @Autowired
    private UserRepository userRepository;

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


    @Action(name = "traceFilter" , mandatoryFields = {"first" , "max"})
    @Transactional
    public List<Trace> traceFilter(HashMap<String,Object> data) throws NotAuthorized {

        if (!isAccessToRead(data,new Trace(),userRepository)) {
            throw new NotAuthorized("access denied for read trace",this,TraceLevel.Warning);
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Trace> q = cb.createQuery(Trace.class);
        Root<Trace> traceRoot = q.from(Trace.class);
        q.select(traceRoot);

        Order order = null;
        if (data.containsKey("order")) {
            Path path = null;
            String orderColumn = (String) data.get("order");
            if (orderColumn.equals("calendar"))
                path = traceRoot.get("calendar");
            if (orderColumn.equals("traceLevel"))
                path = traceRoot.get("traceLevel");

            if (path != null) {
                if (data.containsKey("desc")) {
                    order = cb.desc(path);
                } else {
                    order = cb.asc(path);
                }
                q.orderBy(order);
            }
        }

        Predicate inPredicate = null;
        if (data.containsKey("filter")){
            String filter = (String) data.get("filter");
            List<TraceLevel> traceLevels = new LinkedList<TraceLevel>(Arrays.asList(new TraceLevel[]{TraceLevel.Debug,TraceLevel.Event,TraceLevel.Warning,TraceLevel.Error,TraceLevel.Critical}));
            TraceLevel traceLevel = TraceLevel.valueOf(filter);
            switch (traceLevel) {
                case Critical:  traceLevels.remove(traceLevels.indexOf(TraceLevel.Error));
                case Error:     traceLevels.remove(traceLevels.indexOf(TraceLevel.Warning));
                case Warning:   traceLevels.remove(traceLevels.indexOf(TraceLevel.Event));
                case Event:     traceLevels.remove(traceLevels.indexOf(TraceLevel.Debug));
                case Debug: break;
            }
            inPredicate = traceRoot.get("traceLevel").in(traceLevels);
        }

        Predicate betweenPredicate = null;
        if (data.containsKey("startDate") && data.containsKey("endDate")) {

            Date startDate = new Date((Integer)data.get("startDate"));
            Date endDate = new Date((Integer)data.get("endDate"));

            betweenPredicate = cb.between(traceRoot.get("calendar"),startDate,endDate);
        }

        if (betweenPredicate != null || inPredicate != null) {
            if (betweenPredicate != null && inPredicate != null)
                q.where(cb.and(betweenPredicate,inPredicate));
            else if (betweenPredicate == null)
                q.where(inPredicate);
            else
                q.where(betweenPredicate);
        }


        int first = (int) data.get("first");
        int max   = (int) data.get("max");

        Query query = entityManager.createQuery(q);
        query.setFirstResult(first);
        query.setMaxResults(max);



        List<Trace> traces = query.getResultList();
        return traces;
    }
}
