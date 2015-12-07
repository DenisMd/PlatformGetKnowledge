package com.getknowledge.platform.modules.bootstrapInfo;

import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.exceptions.ParseException;
import com.getknowledge.platform.modules.bootstrapInfo.states.BootstrapResult;
import com.getknowledge.platform.modules.bootstrapInfo.states.BootstrapState;
import com.getknowledge.platform.modules.trace.Trace;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.utils.ModuleLocator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Service("BootstrapInfoService")
public class BootstrapInfoService extends AbstractService {

    @Autowired
    private ModuleLocator moduleLocator;

    @Autowired
    private BootstrapInfoRepository repository;

    @Autowired
    private TraceService log;

    @Action(name = "do")
    public BootstrapResult doBootstrap(HashMap<String, Object> data) throws ParseException {
        List<BootstrapService> bootstrapServices = moduleLocator.findAllBootstrapServices();
        bootstrapServices.sort(
                new Comparator<BootstrapService>() {
                    @Override
                    public int compare(BootstrapService o1, BootstrapService o2) {
                        if (o1.getBootstrapInfo().getOrder() > o2.getBootstrapInfo().getOrder()) {
                            return 1;
                        }

                        if (o1.getBootstrapInfo().getOrder() < o2.getBootstrapInfo().getOrder()) {
                            return -1;
                        }
                        return 0;
                    }
                }
        );
        for (BootstrapService bootstrapService : bootstrapServices) {
            BootstrapInfo bootstrapInfo = null;
            try {
                bootstrapInfo = repository.getSingleEntityByFieldAndValue("name", bootstrapService.getBootstrapInfo().getName());
                if (bootstrapInfo != null && bootstrapInfo.getBootstrapState() == BootstrapState.Completed && !bootstrapInfo.isRepeat()) {
                    continue;
                } else {
                    bootstrapService.bootstrap(data);
                    bootstrapInfo.setBootstrapState(BootstrapState.Completed);
                    repository.update(bootstrapInfo);
                }
            } catch (Exception e) {
                if (bootstrapInfo != null) {
                    bootstrapInfo.setBootstrapState(BootstrapState.Failed);
                    bootstrapInfo.setErrorMessage(e.getMessage());
                    bootstrapInfo.setStackTrace(ExceptionUtils.getStackTrace(e));
                    log.logException("Bootstrap service : " + bootstrapInfo.getName(), e , TraceLevel.Warning);
                    repository.update(bootstrapInfo);
                }
            }
        }

        return BootstrapResult.Complete;
    }
}
