package com.getknowledge.modules.attachements;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;

@Entity
@ModuleInfo(repositoryName = "FileAttachmentRepository")
public class FileAttachment extends AbstractEntity{
    @Basic(fetch= FetchType.LAZY)
    @Column(name = "attach_files")
    @Lob
    @JsonIgnore
    byte [] data;

    @Column(name = "file_name")
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
