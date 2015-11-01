package com.getknowledge.modules.video;

import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.user.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "video")
@ModuleInfo(repositoryName = "VideoRepository" , serviceName = "VideoService")
public class Video extends AbstractEntity{

    @Column(name = "video_name")
    private String videoName;

    @Column(length = 2000)
    private String link;

    @Column(name = "allow_every_one")
    private boolean allowEveryOne = false;

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowCreateEveryOne = false;
        if (!allowEveryOne) {
            authorizationList.getPermissionsForRead().add(new Permission("VideoRead"));
        }
        return null;
    }
}
