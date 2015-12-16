package com.getknowledge.platform.base.services;

import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;

import java.util.HashMap;

public interface BootstrapService {
    void bootstrap(HashMap<String, Object> map) throws Exception;
    BootstrapInfo getBootstrapInfo();
}
