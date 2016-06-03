package com.getknowledge.modules.courses.tutorial.homeworks;

import com.getknowledge.modules.courses.CourseService;
import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.courses.tutorial.TutorialRepository;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.modules.video.Video;
import com.getknowledge.modules.video.VideoRepository;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@Service("HomeWorkService")
public class HomeWorkService extends AbstractService {

    @Autowired
    private TutorialRepository tutorialRepository;

    @Autowired
    private HomeWorkRepository homeWorkRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private TraceService trace;

    @Action(name = "createHomeWork" , mandatoryFields = {"tutorialId","name"})
    @Transactional
    public Result createTutorial(HashMap<String , Object> data) {
        Tutorial tutorial = tutorialRepository.read(longFromField("tutorialId", data));

        if (tutorial == null || !courseService.isUserHasAccessToCourse(userInfoRepository.getCurrentUser(data),tutorial.getCourse())){
            return Result.AccessDenied();
        }

        HomeWork homeWork = homeWorkRepository.createHomeWork(tutorial,(String)data.get("name"));
        Result result1 = Result.Complete();
        result1.setObject(homeWork.getId());
        return result1;
    }

    @Action(name = "getHomeWorks" , mandatoryFields = {"tutorialId"})
    @Transactional
    public List<HomeWork> getTutorial(HashMap<String,Object> data) throws NotAuthorized {
        Tutorial tutorial = tutorialRepository.read(longFromField("tutorialId", data));

        if (tutorial == null || !courseService.isUserHasAccessToCourse(userInfoRepository.getCurrentUser(data),tutorial.getCourse())){
            throw new NotAuthorized("Access denied for user");
        }

        return tutorial.getHomeWorks();
    }

    @Action(name = "getVideo" , mandatoryFields = {"homeWorkId"})
    @Transactional
    public Video getVideo(HashMap<String,Object> data) {
        HomeWork homeWork = homeWorkRepository.read(longFromField("homeWorkId", data));
        if (homeWork == null) {
            return null;
        }

        if (!courseService.isUserHasAccessToCourse(userInfoRepository.getCurrentUser(data), homeWork.getTutorial().getCourse())) {
            return null;
        }

        return homeWork.getVideo();
    }

    @Action(name = "getHomeWorkText" , mandatoryFields = {"homeWorkId"})
    @Transactional
    public String getTutorialText(HashMap<String,Object> data) {
        HomeWork homeWork = homeWorkRepository.read(longFromField("homeWorkId", data));
        if (homeWork == null) {
            return null;
        }

        if (!courseService.isUserHasAccessToCourse(userInfoRepository.getCurrentUser(data), homeWork.getTutorial().getCourse())) {
            return null;
        }

        return homeWork.getData();
    }

    @Action(name = "updateTutorial" , mandatoryFields = {"homeWorkId"})
    @Transactional
    public Result updateTutorial(HashMap<String,Object> data) {
        HomeWork homeWork = homeWorkRepository.read(longFromField("homeWorkId", data));
        if (homeWork == null) {
            return Result.NotFound();
        }

        if (!isAccessToEdit(data,homeWork)) {
            return Result.NotAuthorized();
        }

        if (data.containsKey("name")) {
            String tutorialName = (String) data.get("name");
            homeWork.setName(tutorialName);
        }

        if (data.containsKey("data")) {
            String tutorialData = (String) data.get("data");
            homeWork.setData(tutorialData);
        }


        homeWork.setLastChangeTime(Calendar.getInstance());
        homeWorkRepository.merge(homeWork);
        Result result = Result.Complete();
        return result;
    }

    @ActionWithFile(name = "uploadVideoTutorial" , mandatoryFields = "tutorialId")
    @Transactional
    public Result uploadVideoIntro(HashMap<String,Object> data, List<MultipartFile> files) {
        HomeWork homeWork = homeWorkRepository.read(longFromField("homeWorkId", data));
        if (homeWork == null) {
            return Result.NotFound();
        }

        if (!isAccessToEdit(data,homeWork)) {
            return Result.NotAuthorized();
        }

        try {
            if (homeWork.getVideo() == null) {
                Video videoTut = new Video();
                videoRepository.create((String) data.get("videoName"),
                        null,
                        files.get(0).getBytes(),
                        homeWork.getTutorial().getCourse().isBase());
                homeWork.setVideo(videoTut);
            } else {
                videoRepository.update((String) data.get("videoName"),files.get(0).getBytes());
            }

            homeWork.setLastChangeTime(Calendar.getInstance());
            homeWorkRepository.merge(homeWork);

        } catch (IOException e) {
            trace.logException("Error read cover for program", e, TraceLevel.Warning,true);
            return Result.Failed();
        }

        return Result.Complete();
    }
}
