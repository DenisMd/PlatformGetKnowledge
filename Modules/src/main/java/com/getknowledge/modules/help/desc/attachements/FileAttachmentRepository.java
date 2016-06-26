package com.getknowledge.modules.help.desc.attachements;


import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("FileAttachmentRepository")
public class FileAttachmentRepository extends BaseRepository<FileAttachment> {
    @Override
    protected Class<FileAttachment> getClassEntity() {
        return FileAttachment.class;
    }
}
