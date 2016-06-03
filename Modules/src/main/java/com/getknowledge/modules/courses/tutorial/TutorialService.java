package com.getknowledge.modules.courses.tutorial;

import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.courses.CourseService;
import com.getknowledge.modules.courses.tutorial.comments.question.TutorialQuestion;
import com.getknowledge.modules.courses.tutorial.comments.question.TutorialQuestionRepository;
import com.getknowledge.modules.courses.tutorial.comments.review.TutorialReview;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.modules.userInfo.post.messages.PostMessage;
import com.getknowledge.modules.video.Video;
import com.getknowledge.modules.video.VideoRepository;
import com.getknowledge.modules.video.comment.VideoComment;
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
    private UserInfoRepository userInfoRepository;

    @Autowired
    private TraceService trace;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private TutorialQuestionRepository tutorialQuestionRepository;

    @Action(name = "createTutorial" , mandatoryFields = {"courseId","name"})
    @Transactional
    public Result createTutorial(HashMap<String , Object> data) {
        Result result = courseService.checkCourseRight(data);
        Course course;
        if (result.getObject() != null)  {
            course = (Course) result.getObject();
        } else {
            return result;
        }

        Tutorial tutorial =tutorialRepository.createTutorial(course, (String) data.get("name"));
        Result result1 = Result.Complete();
        result1.setObject(tutorial.getId());
        return result1;
    }

    @Action(name = "getTutorial" , mandatoryFields = {"courseId" , "orderNumber"})
    @Transactional
    public Tutorial getTutorial(HashMap<String,Object> data) throws PlatformException {
        UserInfo userInfo = userInfoRepository.getCurrentUser(data);
        if (userInfo == null) {
            return null;
        }

        Long courseId = new Long((Integer)data.get("courseId"));
        Integer orderNumber = (Integer)data.get("orderNumber");
        try {
            Tutorial tutorial = tutorialRepository.getTutorial(courseId,orderNumber);

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

        if (!courseService.isUserHasAccessToCourse(userInfoRepository.getCurrentUser(data),tutorial.getCourse())) {
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

        if (!courseService.isUserHasAccessToCourse(userInfoRepository.getCurrentUser(data),tutorial.getCourse())) {
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
    @Transactional
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
                videoRepository.create((String) data.get("videoName"),
                        null,
                        files.get(0).getBytes(),
                        tutorial.getCourse().isBase());
                tutorial.setVideo(videoTut);
            } else {
                videoRepository.update((String) data.get("videoName"),files.get(0).getBytes());
            }

            tutorial.setLastChangeTime(Calendar.getInstance());
            tutorialRepository.merge(tutorial);

        } catch (IOException e) {
            trace.logException("Error upload video tutorial  for tutorial with id : " + tutorial.getId() , e, TraceLevel.Error,true);
            return Result.Failed();
        }

        return Result.Complete();
    }


    @Action(name = "getQuestion" , mandatoryFields = {"tutorialId","first","max"})
    @Transactional
    public List<TutorialQuestion> getQuestion(HashMap<String,Object> data){
        Tutorial tutorial = tutorialRepository.read(longFromField("tutorialId",data));
        if (tutorial == null)
            return null;

        int first = (int) data.get("first");
        int max = (int) data.get("max");

        if (!courseService.isUserHasAccessToCourse(userInfoRepository.getCurrentUser(data),tutorial.getCourse())) {
            return null;
        }

        return tutorialRepository.getQuestion(tutorial,first,max);
    }

    @Action(name = "addQuestion" , mandatoryFields = {"tutorialId","text"})
    @Transactional
    public Result addPost(HashMap<String,Object> data){
        Tutorial tutorial = tutorialRepository.read(longFromField("tutorialId",data));
        if (tutorial == null)
            return Result.NotFound();

        UserInfo currentUser = userInfoRepository.getCurrentUser(data);
        if (!courseService.isUserHasAccessToCourse(currentUser,tutorial.getCourse())) {
            return null;
        }


        String textMessage = (String) data.get("text");
        tutorialQuestionRepository.createMessage(currentUser,tutorial,textMessage);

        return Result.Complete();
    }

    @Action(name = "addCommentToQuestion" , mandatoryFields = {"questionId","text"})
    @Transactional
    public Result addCommentToPost(HashMap<String,Object> data){
        UserInfo currentUser = userInfoRepository.getCurrentUser(data);
        if (currentUser == null)
            return  Result.NotAuthorized();

        TutorialQuestion tutorialQuestion = tutorialQuestionRepository.read(longFromField("questionId",data));
        if (tutorialQuestion == null) {
            return Result.NotFound();
        }
        String textMessage = (String) data.get("text");
        tutorialQuestionRepository.createComment(currentUser,tutorialQuestion,textMessage);

        return Result.Complete();
    }

    @Action(name = "getReviews" , mandatoryFields = {"tutorialId","first","max"})
    @Transactional
    public List<TutorialReview> getReviews(HashMap<String,Object> data){
        Tutorial tutorial = tutorialRepository.read(longFromField("tutorialId",data));
        if (tutorial == null)
            return null;

        int first = (int) data.get("first");
        int max = (int) data.get("max");

        if (!courseService.isUserHasAccessToCourse(userInfoRepository.getCurrentUser(data),tutorial.getCourse())) {
            return null;
        }

        return tutorialRepository.getReviews(tutorial,first,max);
    }

}
