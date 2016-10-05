package com.getknowledge.modules.video;

import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.courses.CourseService;
import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.courses.tutorial.homeworks.HomeWork;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.modules.video.comment.VideoComment;
import com.getknowledge.modules.video.comment.VideoCommentRepository;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.*;
import com.getknowledge.platform.exceptions.AccessDeniedException;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import org.apache.commons.io.IOUtils;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@Service("VideoService")
public class VideoService extends AuthorizedService<Video> implements BootstrapService,VideoLinkService,ImageService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private VideoCommentRepository videoCommentRepository;

    @Autowired
    private TraceService traceService;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (videoRepository.count() == 0) {
            Ini ini = new Ini(getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/mainVideos/videoBootstrap"));
            for (String sectionName : ini.keySet()) {
                Profile.Section section = ini.get(sectionName);
                String name = section.get("name",String.class);
                String link = section.get("link",String.class);

                Video video = videoRepository.create(name, link);

                String videoCover = section.get("cover",String.class);

                try (InputStream is = getClass().getClassLoader().getResourceAsStream(videoCover)) {
                    video.setCover(IOUtils.toByteArray(is));
                    videoRepository.merge(video);
                }
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

    //Максимальный размер файла 500 мб
    @ActionWithFile(name = "uploadVideo" , mandatoryFields = {"videoId"} , maxSize = 512_000)
    @Transactional
    public Result uploadVideo(HashMap<String,Object> data, List<MultipartFile> fileList) {
        Long videoId = longFromField("videoId",data);
        Video video = videoRepository.read(videoId);
        if (video == null){
            return Result.Failed();
        }
        UserInfo userInfo = userInfoRepository.getCurrentUser(data);
        if (userInfo == null || !isAccessForEdit(userInfo.getUser(),video)) {
            return Result.AccessDenied();
        }

        return videoRepository.uploadVideo(video,userInfo,fileList.get(0));
    }

    @ActionWithFile(name = "uploadCover" , mandatoryFields = {"videoId"})
    @Transactional
    public Result uploadCover(HashMap<String,Object> data, List<MultipartFile> fileList) {
        Long videoId = longFromField("videoId",data);
        Video video = videoRepository.read(videoId);
        if (video == null){
            return Result.Failed();
        }
        UserInfo userInfo = userInfoRepository.getCurrentUser(data);
        if (userInfo == null || !isAccessForEdit(userInfo.getUser(),video)) {
            return Result.AccessDenied();
        }

        try {
            video.setCover(fileList.get(0).getBytes());
            videoRepository.merge(video);
        } catch (IOException io) {
            traceService.logException("Error upload cover for video " + video.getId(),io, TraceLevel.Error,true);
        }


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
    public boolean isAccessForRead(User currentUser, Video entity) {
        if (entity == null) return false;
        if (entity.isAllowEveryOne()) return true;

        boolean checkPermissions = entity.getAuthorizationList().isAccessRead(currentUser);
        if (checkPermissions) return true;

        UserInfo userInfo = userInfoRepository.getUserInfoByUser(currentUser);
        Course course = videoRepository.findCourseByVideo(entity);

        if(!courseService.isUserHasAccessToCourse(userInfo,course))
            return false;

        return true;
    }

    @Override
    @Transactional
    public boolean isAccessForEdit(User currentUser, Video entity) {
        if (currentUser == null) return false;
        if (entity == null) return false;

        boolean checkPermissions = entity.getAuthorizationList().isAccessEdit(currentUser);
        if (checkPermissions) return true;

        List<Course> courses = entityManager.createQuery("select c from Course c where c.intro.id = :id")
                .setParameter("id",entity.getId()).getResultList();
        if (courses.isEmpty()) {
            List<Tutorial> tutorials = entityManager.createQuery("select t from Tutorial t where t.video.id = :id")
                    .setParameter("id",entity.getId()).getResultList();
            if (tutorials.isEmpty()) {
                List<HomeWork> homeWorks = entityManager.createQuery("select hw from HomeWork hw where hw.video.id = :id")
                        .setParameter("id",entity.getId()).getResultList();
                if (homeWorks.isEmpty()) {
                    return false;
                }
                return homeWorks.get(0).getTutorial().getCourse().getAuthor().getUser().equals(currentUser);
            }
            return tutorials.get(0).getCourse().getAuthor().getUser().equals(currentUser);
        }

        return courses.get(0).getAuthor().getUser().equals(currentUser);
    }
}
