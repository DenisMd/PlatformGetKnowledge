package com.getknowledge.platform.modules.service;

import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(repositoryName = "ServiceRepository" ,serviceName = "ServiceService")
public class Service extends AbstractEntity {

    static class ActionInfo {
        private String type; //action actionWithFile Task
        private String name;
        private List<String> mandatoryFields = new ArrayList<>();

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getMandatoryFields() {
            return mandatoryFields;
        }

        public void setMandatoryFields(List<String> mandatoryFields) {
            this.mandatoryFields = mandatoryFields;
        }
    }

    private String name;

    private List<ActionInfo> actionInfos = new ArrayList<>();

    public List<ActionInfo> getActionInfos() {
        return actionInfos;
    }

    public void setActionInfos(List<ActionInfo> actionInfos) {
        this.actionInfos = actionInfos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
