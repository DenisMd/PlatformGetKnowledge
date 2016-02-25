package com.getknowledge.modules.help.desc.attachements;


import com.getknowledge.modules.help.desc.HpMessage;
import com.getknowledge.platform.base.entities.AbstractEntity;

import javax.persistence.*;

@Entity
public class FileAttachment {

    @ManyToOne
    private HpMessage message;

    @Basic(fetch= FetchType.LAZY)
    @Column(name = "attach_files")
    @Lob
    byte [] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HpMessage getMessage() {
        return message;
    }

    public void setMessage(HpMessage message) {
        this.message = message;
    }
}