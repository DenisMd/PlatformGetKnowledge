package com.getknowledge.modules.courses.tutorial;

import com.getknowledge.modules.courses.CourseService;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.modules.video.Video;
import com.getknowledge.modules.video.VideoRepository;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@Service("TutorialService")
public class TutorialService extends AbstractService {

    @Autowired
    private TutorialRepository tutorialRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private TraceService trace;

    @Autowired
    private VideoRepository videoRepository;

    @Action(name = "getTutorial" , mandatoryFields = {"courseId" , "orderNumber"})
    @Transactional
    public Tutorial getTutorial(HashMap<String,Object> data) throws PlatformException {
        UserInfo userInfo = userInfoService.getAuthorizedUser(data);
        if (userInfo == null) {
            return null;
        }

        Long courseId = new Long((Integer)data.get("courseId"));
        Integer orderNumber = (Integer)data.get("orderNumber");
        try {
            Tutorial tutorial = (Tutorial) entityManager.createQuery("select t from Tutorial t where t.course.id = :courseId and t.orderNumber = :orderNumber")
                    .setParameter("courseId", courseId)
                    .setParameter("orderNumber", orderNumber).getSingleResult();

            if (!courseService.isUserHasAccessToCourse(userInfo,tutorial.getCourse())) {
                throw new NotAuthorized("error read tutorial");
            }

            return tutorial;

        } catch (NoResultException no) {
            return null;
        }
    }

    @Action(name = "getVideo" , mandatoryFields = {"tutorialId"})
    @Transactional
    public Video getVideo(HashMap<String,Object> data) {
        Tutorial tutorial = tutorialRepository.read(longFromField("tutorialId", data));
        if (tutorial == null) {
            return null;
        }

        if (!courseService.isUserHasAccessToCourse(userInfoService.getAuthorizedUser(data),tutorial.getCourse())) {
            return null;
        }

        return tutorial.getVideo();
    }

    @Action(name = "getTutorialText" , mandatoryFields = {"tutorialId"})
    @Transactional
    public String getTutorialText(HashMap<String,Object> data) {
        Tutorial tutorial = tutorialRepository.read(longFromField("tutorialId", data));
        if (tutorial == null) {
            return null;
        }

        if (!courseService.isUserHasAccessToCourse(userInfoService.getAuthorizedUser(data),tutorial.getCourse())) {
            return null;
        }

        return tutorial.getData();
    }

    @Action(name = "updateTutorial" , mandatoryFields = {"tutorialId"})
    @Transactional
    public Result updateTutorial(HashMap<String,Object> data) {
        Tutorial tutorial = tutorialRepository.read(longFromField("tutorialId", data));
        if (tutorial == null) {
            return Result.NotFound();
        }

        if (!isAccessToEdit(data,tutorial)) {
            return Result.NotAuthorized();
        }

        if (data.containsKey("name")) {
            String tutorialName = (String) data.get("name");
            tutorial.setName(tutorialName);
        }

        if (data.containsKey("data")) {
            String tutorialData = (String) data.get("data");
            tutorial.setData(tutorialData);
        }

        if (data.containsKey("orderNumber")) {
            Integer orderNumber = (Integer) data.get("orderNumber");
            if (tutorial.getOrderNumber() != orderNumber) {
                if (tutorial.getOrderNumber() < orderNumber) {
                    entityManager.createQuery("update Tutorial t set t.orderNumber = t.orderNumber-1, t.lastChangeTime = :current_date where t.orderNumber > :orderParam and t.orderNumber <= :orderParam2")
                            .setParameter("orderParam" , tutorial.getOrderNumber())
                            .setParameter("orderParam2" , orderNumber)
                            .setParameter("current_date", Calendar.getInstance()).executeUpdate();

                    Integer min = (Integer) entityManager.createQuery("select min(t.orderNumber) from Tutorial  t where t.orderNumber > :orderParam")
                            .setParameter("orderParam" , orderNumber).getSingleResult();
                    if (min == null)
                        tutorial.setOrderNumber((Integer) entityManager.createQuery("select max(t.orderNumber) from Tutorial  t").getSingleResult() + 1);
                    else
                        tutorial.setOrderNumber(min-1);


                } else {
                    entityManager.createQuery("update Tutorial t set t.orderNumber = t.orderNumber+1,t.lastChangeTime = :current_date where t.orderNumber < :orderParam and t.orderNumber >= :orderParam2")
                            .setParameter("orderParam", tutorial.getOrderNumber())
                            .setParameter("orderParam2", orderNumber)
                            .setParameter("current_date" , Calendar.getInstance()).executeUpdate();

                    Integer max = (Integer) entityManager.createQuery("select max(t.orderNumber) from Tutorial  t where t.orderNumber < :orderParam")
                            .setParameter("orderParam" , orderNumber).getSingleResult();
                    if (max == null)
                        tutorial.setOrderNumber((Integer) entityManager.createQuery("select min(t.orderNumber) from Tutorial  t").getSingleResult() - 1);
                    else
                        tutorial.setOrderNumber(max+1);
                }
            }
        }

        tutorial.setLastChangeTime(Calendar.getInstance());
        tutorialRepository.merge(tutorial);
        Result result = Result.Complete();
        result.setObject(tutorial.getOrderNumber());
        return result;
    }

    @ActionWithFile(name = "uploadVideoTutorial" , mandatoryFields = "tutorialId")
    public Result uploadVideoIntro(HashMap<String,Object> data, List<MultipartFile> files) {
        Tutorial tutorial = tutorialRepository.read(longFromField("tutorialId", data));
        if (tutorial == null) {
            return Result.NotFound();
        }

        if (!isAccessToEdit(data,tutorial)) {
            return Result.NotAuthorized();
        }

        try {
            if (tutorial.getVideo() == null) {
                Video videoTut = new Video();
                videoTut.setAllowEveryOne(tutorial.getCourse().isBase());
                videoTut.setVideoName((String) data.get("videoName"));
                videoTut.setCover(files.get(0).getBytes());
                videoRepository.create(videoTut);
                tutorial.setVideo(videoTut);
            } else {
                Video intro = tutorial.getVideo();
                intro.setVideoName((String) data.get("videoName"));
                intro.setCover(files.get(0).getBytes());
                videoRepository.merge(intro);
            }

            tutorial.setLastChangeTime(Calendar.getInstance());
            tutorialRepository.merge(tutorial);

        } catch (IOException e) {
            trace.logException("Error read cover for program" , e, TraceLevel.Warning);
            return Result.Failed();
        }

        return Result.Complete();
    }

}
