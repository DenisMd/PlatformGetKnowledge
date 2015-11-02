package com.getknowledge.modules.video;

import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("VideoService")
public class VideoService extends AbstractService implements BootstrapService {

    @Autowired
    private VideoRepository videoRepository;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (videoRepository.count(Video.class) == 0) {
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
}
