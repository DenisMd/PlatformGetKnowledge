package com.getknowledge.modules.socialLinks;

import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "social_link")
@ModuleInfo(repositoryName = "SocialLinkRepository" , serviceName = "SocialLinksService")
public class SocialLink extends AbstractEntity {

    private String name;

    @Column(length = 500)
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowCreateEveryOne = false;
        authorizationList.allowReadEveryOne = true;

        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditSocialLinks.getName()));

        return authorizationList;
    }
}
