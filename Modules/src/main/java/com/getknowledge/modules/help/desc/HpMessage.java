package com.getknowledge.modules.help.desc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.help.desc.attachements.FileAttachment;
import com.getknowledge.modules.help.desc.type.HpMessageType;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;
import com.getknowledge.platform.modules.user.User;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hp_messages")
@ModuleInfo(repositoryName =  "HPMessageRepository" , serviceName = "HPMessageService")
public class HpMessage extends AbstractEntity{

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    HpMessageType type = HpMessageType.Question;

    @Column(length = 120 , nullable = false)
    private String title;

    @Column(columnDefinition = "Text" , name = "message" , nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(nullable = true)
    private User user;

    @OneToMany(mappedBy = "message")
    @JsonIgnore
    private List<FileAttachment> files = new ArrayList<>();

    public List<FileAttachment> getFiles() {
        return files;
    }

    public void setFiles(List<FileAttachment> files) {
        this.files = files;
    }

    private boolean reply = false;

    private boolean checked = false;

    public boolean isReply() {
        return reply;
    }

    public void setReply(boolean reply) {
        this.reply = reply;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

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

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.getPermissionsForRead().add(new Permission(PermissionNames.ReadHpMessage.getName()));
        return authorizationList;
    }
}
