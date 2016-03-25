package com.getknowledge.platform.modules.bootstrapInfo;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.states.BootstrapState;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.utils.ModuleLocator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Repository("BootstrapInfoRepository")
public class BootstrapInfoRepository extends BaseRepository<BootstrapInfo> {

    @Override
    protected Class<BootstrapInfo> getClassEntity() {
        return BootstrapInfo.class;
    }

    @Autowired
    private ModuleLocator moduleLocator;

    @Autowired
    private TraceService trace;

    private BootstrapInfo createIfNotExist(BootstrapInfo object) {
        if (object.getName() == null) return null;
        BootstrapInfo bootstrapInfo = getSingleEntityByFieldAndValue("name" , object.getName());
        if (bootstrapInfo != null) return bootstrapInfo;
        object.setBootstrapState(BootstrapState.NotComplete);
        super.create(object);
        return object;
    }

    public void createFromServices(){
        moduleLocator.findAllBootstrapServices().stream().forEach
                (bi -> createIfNotExist(bi.getBootstrapInfo()));
    }

    public void doBootstrap(BootstrapService bootstrapService,HashMap<String,Object> data) {
        BootstrapInfo bootstrapInfo = null;
        try {
            bootstrapInfo = getSingleEntityByFieldAndValue("name", bootstrapService.getBootstrapInfo().getName());
            if (!(bootstrapInfo != null && bootstrapInfo.getBootstrapState() == BootstrapState.Completed && !bootstrapInfo.isRepeat())) {
                bootstrapInfo.setStartTime(Calendar.getInstance());
                bootstrapService.bootstrap(data);
                bootstrapInfo.setErrorMessage(null);
                bootstrapInfo.setStackTrace(null);
                bootstrapInfo.setBootstrapState(BootstrapState.Completed);
                merge(bootstrapInfo);
            }
        } catch (Exception e) {
            if (bootstrapInfo != null) {
                bootstrapInfo.setBootstrapState(BootstrapState.Failed);
                bootstrapInfo.setErrorMessage(e.getMessage());
                bootstrapInfo.setStackTrace(ExceptionUtils.getStackTrace(e));
                trace.logException("Bootstrap service : " + bootstrapInfo.getName(),e,TraceLevel.Error);
                merge(bootstrapInfo);
            }
        }
    }

    @Override
    public List<BootstrapInfo> list() {
        return entityManager.createQuery("select b from BootstrapInfo b order by b.order asc").getResultList();
    }
}
