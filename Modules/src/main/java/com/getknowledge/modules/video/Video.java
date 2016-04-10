package com.getknowledge.modules.video;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.*;
import java.util.Calendar;

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

    @Column(name = "upload_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar uploadTime;

    public Calendar getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Calendar uploadTime) {
        this.uploadTime = uploadTime;
    }

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
        //Доступ к видео контролирует videoService
        AuthorizationList authorizationList = new AuthorizationList();

        //позволяет читать видео с общим доступом
        if (allowEveryOne) {
            authorizationList.allowReadEveryOne = true;
        }
        authorizationList.getPermissionsForCreate().add(new Permission(PermissionNames.UploadVideos));
        authorizationList.allowUseAuthorizedService = true;
        return authorizationList;
    }
}
