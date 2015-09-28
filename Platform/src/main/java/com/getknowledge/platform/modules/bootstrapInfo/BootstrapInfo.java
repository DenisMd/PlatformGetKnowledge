package com.getknowledge.platform.modules.bootstrapInfo;


import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.modules.bootstrapInfo.states.BootstrapState;

import javax.persistence.*;

@Entity
@Table(name = "sys_bootstrap")
@ModuleInfo(repositoryName = "BootstrapInfoRepository" , serviceName = "BootstrapInfoService")
public class BootstrapInfo extends AbstractEntity {

    @Column(name = "bootstrap_order")
    private Integer order = 0;

    private String name;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private BootstrapState bootstrapState;

    @Column(name = "error_message" , length = 2000)
    private String errorMessage;

    public BootstrapState getBootstrapState() {
        return bootstrapState;
    }

    public void setBootstrapState(BootstrapState bootstrapState) {
        this.bootstrapState = bootstrapState;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
