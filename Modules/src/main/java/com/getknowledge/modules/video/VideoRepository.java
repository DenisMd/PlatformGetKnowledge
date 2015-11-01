package com.getknowledge.modules.video;

import com.getknowledge.platform.base.repositories.FileLinkRepository;
import org.springframework.stereotype.Repository;

@Repository("VideoRepository")
public class VideoRepository extends FileLinkRepository<Video>{

    @Override
    public String getFileLink(long id) {
        Video video = read(id , Video.class);
        return video == null ? null : "/WEB-INF/video/" +video.getLink();
    }

}
