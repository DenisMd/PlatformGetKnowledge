package com.getknowledge.modules.messages.attachments;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("PostImageRepository")
public class PostImageRepository extends BaseRepository<PostImage> {
    @Override
    protected Class<PostImage> getClassEntity() {
        return PostImage.class;
    }
}
