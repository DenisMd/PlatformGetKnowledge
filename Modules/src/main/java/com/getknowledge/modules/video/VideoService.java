package com.getknowledge.modules.video;

import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.base.services.FileLinkService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("VideoService")
public class VideoService extends AbstractService implements BootstrapService , FileLinkService{

    @Autowired
    private VideoRepository videoRepository;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (videoRepository.count() == 0) {
            Video video = new Video();
            video.setAllowEveryOne(true);
            video.setVideoName("IndexVideo1");
            video.setLink("index/video1.mp4");
            videoRepository.create(video);
            Video video2 = new Video();
            video2.setAllowEveryOne(true);
            video2.setVideoName("IndexVideo2.mp4");
            video2.setLink("index/video2");
            videoRepository.create(video2);
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("VideoBootstrap");
        bootstrapInfo.setOrder(1);
        return bootstrapInfo;
    }

    @Override
    public String getFileLink(long id) {
        Video video = videoRepository.read(id);
        return video == null ? null : "/WEB-INF/video/" +video.getLink();
    }
}
