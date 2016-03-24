package com.getknowledge.modules.messages.attachments;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("AttachmentImageRepository")
public class AttachmentImageRepository extends BaseRepository<AttachmentImage> {
    @Override
    protected Class<AttachmentImage> getClassEntity() {
        return AttachmentImage.class;
    }
}
