package com.getknowledge.modules.video;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.platform.auth.PermissionNames;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "video")
@ModuleInfo(repositoryName = "VideoRepository" , serviceName = "VideoService")
public class Video extends AbstractEntity{

    @Column(name = "video_name" ,nullable = false)
    private String videoName;

    @Column(length = 2000 , nullable = false)
    private String link;

    @Basic(fetch= FetchType.LAZY)
    @Lob @Column(name="cover")
    @JsonIgnore
    private byte[] cover;

    @Column(name = "allow_every_one" , nullable = false)
    private boolean allowEveryOne = false;

    @Column(name = "upload_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar uploadTime;

    @Column(name = "size")
    private Long size;

    private Long duration;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VideoType videoType = VideoType.LocalVideoFile;

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

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

    public VideoType getVideoType() {
        return videoType;
    }

    public void setVideoType(VideoType videoType) {
        this.videoType = videoType;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        //Доступ к видео контролирует videoService
        AuthorizationList authorizationList = new AuthorizationList();

        //позволяет читать видео с общим доступом
        if (allowEveryOne) {
            authorizationList.allowReadEveryOne = true;
        }
        authorizationList.getPermissionsForCreate().add(new Permission(PermissionNames.UploadVideos()));
        authorizationList.getPermissionsForRead().add(new Permission(PermissionNames.ReadVideo()));
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditVideo()));
        authorizationList.allowUseAuthorizedService = true;
        return authorizationList;
    }
}
