package com.getknowledge.modules.news;

import com.getknowledge.modules.platform.auth.PermissionNames;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "news")
@ModuleInfo(repositoryName = "NewsRepository", serviceName = "NewsService")
public class News extends AbstractEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "Text" , nullable = false)
    private String message;

    @Column(name = "post_date" , nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar postDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Calendar getPostDate() {
        return postDate;
    }

    public void setPostDate(Calendar postDate) {
        this.postDate = postDate;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowReadEveryOne = true;
        authorizationList.getPermissionsForCreate().add(new Permission(PermissionNames.EditNews()));
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditNews()));
        authorizationList.getPermissionsForRemove().add(new Permission(PermissionNames.EditNews()));

        return authorizationList;
    }

}
