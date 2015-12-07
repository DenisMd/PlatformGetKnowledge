package com.getknowledge.platform.modules.permission;

import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.exceptions.ParseException;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.bootstrapInfo.states.BootstrapState;
import com.getknowledge.platform.modules.permission.names.PermissionNames;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("PermissionService")
public class PermissionService implements BootstrapService{

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private TraceService traceService;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws ParseException {
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.ReadUserInfo.name()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.VideoRead.name()));

        throw new ParseException("Test Exception message" , traceService, TraceLevel.Error);
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("PermissionService");
        bootstrapInfo.setBootstrapState(BootstrapState.NotComplete);
        bootstrapInfo.setRepeat(true);
        return bootstrapInfo;
    }
}
