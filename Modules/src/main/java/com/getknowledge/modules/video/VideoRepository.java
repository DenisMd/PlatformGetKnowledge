package com.getknowledge.modules.video;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("VideoRepository")
public class VideoRepository extends BaseRepository<Video> {
    @Override
    protected Class<Video> getClassEntity() {
        return Video.class;
    }
}
