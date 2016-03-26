package com.getknowledge.modules.video;

import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.courses.CourseService;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.modules.video.comment.VideoComment;
import com.getknowledge.modules.video.comment.VideoCommentRepository;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.base.services.VideoLinkService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.user.User;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@Service("VideoService")
public class VideoService extends AbstractService implements BootstrapService,VideoLinkService,ImageService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private VideoCommentRepository videoCommentRepository;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (videoRepository.count() == 0) {
            Ini ini = new Ini(getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/mainVideos/videoBootstrap"));
            for (String sectionName : ini.keySet()) {
                Profile.Section section = ini.get(sectionName);
                String name = section.get("name",String.class);
                String link = section.get("link",String.class);
                videoRepository.create(name, link);
            }
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("Video service");
        bootstrapInfo.setOrder(1);
        return bootstrapInfo;
    }

    @ActionWithFile(name = "uploadVideo" , mandatoryFields = {"videoId"})
    @Transactional
    public Result uploadVideo(HashMap<String,Object> data, List<MultipartFile> fileList) {
        Long videoId = longFromField("videoId",data);
        Video video = videoRepository.read(videoId);
        if (video == null){
            return Result.Failed();
        }
        UserInfo userInfo = userInfoService.getAuthorizedUser(data);
        if (userInfo == null || !isAccessToEdit(data,video)) {
            return Result.AccessDenied();
        }

        videoRepository.uploadVideo(video,userInfo,fileList.get(0));

        return Result.Complete();
    }

    @Action(name = "getComments" , mandatoryFields = {"videoId","first","max"})
    @Transactional
    public List<VideoComment> getComments(HashMap<String,Object> data) {
        Long videoId = longFromField("videoId",data);
        Integer first = (Integer) data.get("first");
        Integer max = (Integer) data.get("max");
        Video video = videoRepository.read(videoId);
        if (video != null)
            return videoRepository.comments(video,first,max);
        return null;
    }

    @Action(name = "addComment" , mandatoryFields = {"videoId","text"})
    @Transactional
    public Result addComment(HashMap<String,Object> data) throws NotAuthorized {
        UserInfo userInfo = userInfoService.getAuthorizedUser(data);
        if (userInfo == null) {
            Result.NotAuthorized();
        }

        Long videoId = longFromField("videoId",data);
        Video video = videoRepository.read(videoId);

        if (video == null){
            Result.NotFound();
        }

        String text = (String) data.get("text");
        if (text.length() > 250) {
            Result.Failed();
        }

        videoCommentRepository.createComment(text,video,userInfo);
        return Result.Complete();
    }

    @Override
    @Transactional
    public String getVideoLink(long id) {
        return videoRepository.getVideoPath(id);
    }

    @Override
    @Transactional
    public byte[] getImageById(long id) {
        Video video = videoRepository.read(id);
        return video == null ? null : video.getCover();
    }

    @Override
    @Transactional
    public boolean isAccessToWatchVideo(long id,User currentUser) {
        Video video = videoRepository.read(id);
        if (video == null) return false;
        if (video.isAllowEveryOne()) return true;

        UserInfo userInfo = userInfoRepository.getUserInfoByUser(currentUser);
        Course course = videoRepository.findCourseByVideo(video);

        if(!courseService.isUserHasAccessToCourse(userInfo,course))
            return false;

        return true;
    }
}
