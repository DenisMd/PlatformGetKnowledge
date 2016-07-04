package com.getknowledge.modules.dictionaries.socialLinks;

import com.getknowledge.modules.platform.auth.PermissionNames;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "social_link")
@ModuleInfo(repositoryName = "SocialLinkRepository" , serviceName = "SocialLinksService")
public class SocialLink extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 500 , nullable = false)
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

        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditSocialLinks()));

        return authorizationList;
    }
}
