package com.getknowledge.modules.video;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.*;

@Entity
@Table(name = "video")
@ModuleInfo(repositoryName = "VideoRepository" , serviceName = "VideoService")
public class Video extends AbstractEntity{

    @Column(name = "video_name")
    private String videoName;

    @Column(length = 2000)
    private String link;

    @Basic(fetch= FetchType.LAZY)
    @Lob @Column(name="cover")
    @JsonIgnore
    private byte[] cover;

    @Column(name = "allow_every_one")
    private boolean allowEveryOne = false;

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public boolean isAllowEveryOne() {
        return allowEveryOne;
    }

    public void setAllowEveryOne(boolean allowEveryOne) {
        this.allowEveryOne = allowEveryOne;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowCreateEveryOne = false;
        if (!allowEveryOne) {
            authorizationList.allowReadEveryOne = false;
        } else {
            authorizationList.allowReadEveryOne = true;
        }
        return authorizationList;
    }
}
