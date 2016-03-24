package com.getknowledge.modules.video;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.DeleteException;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;

@Repository("VideoRepository")
public class VideoRepository extends BaseRepository<Video> {

    @Override
    protected Class<Video> getClassEntity() {
        return Video.class;
    }

    @Value("${video.path}")
    private String pathToVideo;

    @Override
    public void remove(Long id) throws PlatformException {
        Video video = read(id);
        if (video == null) throw new DeleteException("Video by id : " + id + " not found");
        String separator = File.separator;
        File file = new File(pathToVideo + File.separator + video.getLink());
        if (file.exists()) {
            boolean result = file.delete();
            if (!result) throw new DeleteException("Can't delete video file " + file.getAbsolutePath(),trace, TraceLevel.Error);
        }

        super.remove(id);
    }

    public void create(String name,String link) {
        Video video = new Video();
        video.setLink(link);
        video.setVideoName(name);
        video.setAllowEveryOne(true);
        create(video);
    }
}
