package com.getknowledge.modules.settings;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "settings")
@ModuleInfo(repositoryName = "SettingsRepository" , serviceName = "SettingsService")
public class Settings extends AbstractEntity {

    private String domain;

    private String version;

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
