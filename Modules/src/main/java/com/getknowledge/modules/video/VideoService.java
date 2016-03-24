package com.getknowledge.modules.video;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.base.services.VideoLinkService;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

@Service("VideoService")
public class VideoService extends AbstractService implements BootstrapService , VideoLinkService,ImageService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private TraceService trace;

    @Autowired
    private UserInfoService userInfoService;

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

    @Value("${video.path}")
    private String pathToVideo;

    @ActionWithFile(name = "uploadVideo" , mandatoryFields = {"videoId"})
    public Result uploadVideo(HashMap<String,Object> data, List<MultipartFile> fileList) {
        Long videoId = new Long((Integer)data.get("videoId"));
        Video video = videoRepository.read(videoId);
        if (video == null){
            return Result.Failed();
        }
        UserInfo userInfo = userInfoService.getAuthorizedUser(data);
        if (userInfo == null || !video.getAuthorizationList().isAccessCreate(userInfo.getUser())) {
            return Result.AccessDenied();
        }

        String separator = File.separator;

        File file = new File(pathToVideo + separator + userInfo.getId());
        if (!file.exists()) {
            trace.log("Create dir for user : " + file.getAbsolutePath(), TraceLevel.Event);
            file.mkdir();
        }

        MultipartFile multipartFile = fileList.get(0);

        if (video.getLink() != null) {
            File oldVideo = new File(pathToVideo + File.separator + video.getLink());
            oldVideo.delete();
        }

        File videoFile = new File(pathToVideo + separator + userInfo.getId() + separator + videoId + "_" + multipartFile.getOriginalFilename());
        if (!videoFile.exists()) {
            try {
                videoFile.createNewFile();
            } catch (IOException e) {
                trace.logException("Error upload video file" , e,TraceLevel.Error);
            }
        }

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(videoFile);
            fos.write(multipartFile.getBytes());
        } catch (FileNotFoundException e) {
            trace.logException("Error upload video file" , e,TraceLevel.Error);
        } catch (IOException e) {
            trace.logException("Error upload video file" , e,TraceLevel.Error);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    trace.logException("Error upload video file" , e,TraceLevel.Error);
                }
            }
        }

        video.setLink(userInfo.getId() + separator + videoId + "_" + multipartFile.getOriginalFilename());
        videoRepository.merge(video);
        trace.log("Video file successfully upload + " + videoFile.getAbsolutePath() , TraceLevel.Event);

        return Result.Complete();
    }

    @Override
    public String getVideoLink(long id) {
        Video video = videoRepository.read(id);
        return video == null ? null : pathToVideo + File.separator + video.getLink();
    }

    @Override
    public byte[] getImageById(long id) {
        Video video = videoRepository.read(id);
        return video == null ? null : video.getCover();
    }
}
