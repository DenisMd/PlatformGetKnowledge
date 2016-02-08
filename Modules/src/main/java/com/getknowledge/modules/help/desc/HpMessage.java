package com.getknowledge.modules.help.desc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.help.desc.type.HpMessageType;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;
import com.getknowledge.platform.modules.user.User;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "hp_messages")
@ModuleInfo(repositoryName =  "HPMessageRepository" , serviceName = "HPMessageService")
public class HpMessage extends AbstractEntity{

    @Enumerated(EnumType.STRING)
    HpMessageType type;

    @Column(length = 120)
    private String title;

    @Column(columnDefinition = "Text" , name = "message")
    private String message;

    @JsonIgnore
    @ElementCollection
    @Basic(fetch=FetchType.LAZY)
    @Column(name = "attach_files")
    @Lob
    private List<byte []> attachFiles;

    @ManyToOne
    @JoinColumn(nullable = true)
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public HpMessageType getType() {
        return type;
    }

    public void setType(HpMessageType type) {
        this.type = type;
    }

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

    public List<byte[]> getAttachFiles() {
        return attachFiles;
    }

    public void setAttachFiles(List<byte[]> attachFiles) {
        this.attachFiles = attachFiles;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.getPermissionsForRead().add(new Permission(PermissionNames.ReadHpMessage.getName()));
        return authorizationList;
    }
}
