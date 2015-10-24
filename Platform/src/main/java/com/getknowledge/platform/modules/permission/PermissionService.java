package com.getknowledge.platform.modules.permission;

import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.bootstrapInfo.states.BootstrapState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("PermissionService")
public class PermissionService implements BootstrapService{

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public void bootstrap(HashMap<String, Object> map) {
        permissionRepository.ifNotExistCreate(new Permission("ReadUserInfo"));
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
