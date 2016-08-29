package com.getknowledge.modules.courses;

import com.getknowledge.modules.courses.changelist.ChangeList;
import com.getknowledge.modules.courses.changelist.ChangeListRepository;
import com.getknowledge.modules.courses.group.GroupCourses;
import com.getknowledge.modules.courses.group.GroupCoursesRepository;
import com.getknowledge.modules.courses.tags.CoursesTag;
import com.getknowledge.modules.courses.tags.CoursesTagRepository;
import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.courses.tutorial.TutorialRepository;
import com.getknowledge.modules.courses.version.Version;
import com.getknowledge.modules.dictionaries.knowledge.Knowledge;
import com.getknowledge.modules.dictionaries.knowledge.KnowledgeRepository;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.dictionaries.language.LanguageRepository;
import com.getknowledge.modules.dictionaries.language.names.Languages;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.modules.video.Video;
import com.getknowledge.modules.video.VideoRepository;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.AuthorizedService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.exceptions.PlatformException;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service("CourseService")
public class CourseService extends AuthorizedService<Course> implements ImageService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private GroupCoursesRepository groupCoursesRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private TraceService trace;

    @Autowired
    private LanguageRepository languageRepository;


    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private TutorialRepository tutorialRepository;

    @Autowired
    private ChangeListRepository changeListRepository;

    public Result checkCourseRight(HashMap<String,Object> data) {
        Long courseId = longFromField("courseId",data);
        Course course = courseRepository.read(courseId);
        if (course == null) {
            return Result.NotFound();
        }

        UserInfo userInfo = userInfoRepository.getCurrentUser(data);

        if (!course.getAuthorizationList().isAccessEdit(userInfo.getUser())) {
            return Result.AccessDenied();
        }

        //Для выпущенных курсов информацию менять нельзя
        if (course.isRelease()) {
            Result result = Result.Failed();
            result.setObject("Course is release");
            return result;
        }

        Result result = Result.Complete();
        result.setObject(course);
        return result;
    }

    public boolean isUserHasAccessToCourse(UserInfo userInfo , Course course) {

        if (Objects.equals(course.getAuthor().getId(), userInfo.getId())) {
            return true;
        }

        if (course.getTesters().contains(userInfo)) {
            return true;
        }

        if (isAccessForEdit(userInfo.getUser(),course)) {
            return true;
        }

        if (!course.isRelease()) {
            return false;
        }

        if (course.isBase()) {
            return true;
        }

        if (userInfo == null) {
            return false;
        }

        if (userInfo.getStudiedCourses().contains(course)){
            return true;
        }

        if (userInfo.getPurchasedCourses().contains(course)){
            return true;
        }

        return false;
    }

    @Action(name = "createCourse" , mandatoryFields = {"name","groupCourseUrl","description","language","base"})
    @Transactional
    public Result createProgram(HashMap<String,Object> data) {
        if (!data.containsKey("principalName"))
            return Result.NotAuthorized();

        UserInfo userInfo = userInfoRepository.getCurrentUser(data);

        Course course = new Course();
        if (!course.getAuthorizationList().isAccessCreate(userInfo.getUser())) {
            return Result.AccessDenied();
        }

        GroupCourses groupCourses =  groupCoursesRepository.getSingleEntityByFieldAndValue("url" , data.get("groupCourseUrl"));
        if (groupCourses == null) {
            trace.log("Group courses not found" , TraceLevel.Warning,false);
            return Result.NotFound();
        }

        Language language = null;
        try {
            language = languageRepository.getLanguage(Languages.valueOf((String) data.get("language")));
        } catch (Exception exception) {
            return Result.NotFound();
        }

        List<String> tags = null;
        if (data.containsKey("tags")) {
            tags = (List<String>) data.get("tags");
        }


        course = courseRepository.createCourse(userInfo,groupCourses,
                                (String)data.get("name"),
                                (String)data.get("description"),
                                language,tags,
                                (Boolean)data.get("base"),false);

        Result result = Result.Complete();
        result.setObject(course.getId());
        return result;
    }

    @Action(name = "updateCourseInformation" , mandatoryFields = {"courseId"})
    @Transactional
    public Result updateProgramInformation(HashMap<String,Object> data) {

        Result result = checkCourseRight(data);
        Course course;
        if (result.getObject() != null)  {
            course = (Course) result.getObject();
        } else {
            return result;
        }

        Language language = null;
        if (data.containsKey("language")) {
            language = languageRepository.getLanguage(Languages.valueOf((String) data.get("language")));
        }

        courseRepository.updateCourse(course,
                                (String)data.get("name"),
                                (String)data.get("description"),
                                (List<String>)data.get("tags"),
                                (List<Integer>)data.get("sourceKnowledge"),
                                (List<Integer>)data.get("requiredKnowledge"),
                                language);

        return Result.Complete();
    }

    @ActionWithFile(name = "uploadCover" , mandatoryFields = "courseId")
    @Transactional
    public Result uploadCover(HashMap<String,Object> data, List<MultipartFile> files) {
        Result result = checkCourseRight(data);
        Course course;
        if (result.getObject() != null)  {
            course = (Course) result.getObject();
        } else {
            return result;
        }

        try {
            course.setCover(files.get(0).getBytes());
            courseRepository.merge(course);
        } catch (IOException e) {
            trace.logException("Error upload cover for course : " + course.getId() , e, TraceLevel.Warning,true);
            return Result.Failed();
        }

        return Result.Complete();
    }

    @ActionWithFile(name = "uploadVideoIntro" , mandatoryFields = "courseId")
    @Transactional
    public Result uploadVideoIntro(HashMap<String,Object> data, List<MultipartFile> files) {
        Result result = checkCourseRight(data);
        Course course;
        if (result.getObject() != null)  {
            course = (Course) result.getObject();
        } else {
            return result;
        }

        try {
            if (course.getIntro() == null) {
                Video intro = videoRepository.create((String) data.get("videoName"), files.get(0).getBytes());
                course.setIntro(intro);
                courseRepository.merge(course);
            } else {
                videoRepository.update((String) data.get("videoName"),files.get(0).getBytes());
            }

        } catch (IOException e) {
            trace.logException("Error uplaod vide intro for cours : " + course.getId() , e, TraceLevel.Error,true);
            return Result.Failed();
        }

        return Result.Complete();
    }

    @Action(name = "release" , mandatoryFields = {"courseId", "version"})
    @Transactional
    public Result release(HashMap<String,Object> data) throws PlatformException {
        Result result = checkCourseRight(data);
        Course course;
        if (result.getObject() != null)  {
            course = (Course) result.getObject();
        } else {
            return result;
        }

        if (course.getBaseCourse() == null) {
            if (!course.isRelease()) {
                ChangeList changeList = changeListRepository.createChangeList(course,"Initialize course");
                courseRepository.releaseBaseCourse(course,changeList);
            }
        } else {
            //Значит мы пытаемся выпустить черновик
            Integer version = (Integer) data.get("version");
            Course baseCourse = course.getBaseCourse();
            switch (version) {
                case 1 :
                    baseCourse.getVersion().setMajorVersion(baseCourse.getVersion().getMajorVersion() + 1);
                    break;
                case 2 :
                    baseCourse.getVersion().setMiddleVersion(baseCourse.getVersion().getMiddleVersion() + 1);
                    break;
                case 3 :
                    baseCourse.getVersion().setMinorVersion(baseCourse.getVersion().getMinorVersion() + 1);
                    break;
                default: return Result.Failed();
            }


            if (data.containsKey("changes")) {
                List<String> changes = (List<String>) data.get("changes");
                ChangeList changeList = changeListRepository.createChangeList(baseCourse, changes);
                baseCourse.getChangeLists().add(changeList);
            }

            courseRepository.mergeDraft(baseCourse,course);
            courseRepository.merge(baseCourse);
            //удаляем черновик
            courseRepository.remove(course.getId());
        }

        return Result.Complete();
    }

    @Action(name = "makeDraft" , mandatoryFields = {"courseId"})
    @Transactional
    public Result makeDraft(HashMap<String,Object> data) {
        Result result = checkCourseRight(data);
        Course course;
        if (result.getObject() != null)  {
            course = (Course) result.getObject();
        } else {
            return result;
        }

        if (!course.isRelease() && course.getDraftCourse() != null) {
            return Result.Failed();
        }

        Course draft = course.clone();
        courseRepository.createDraft(course,draft);

        result = Result.Complete();
        result.setObject(draft.getId());
        return result;
    }

    @Action(name = "userHasAccessToCourse" , mandatoryFields = {"courseId"})
    @Transactional
    public Boolean userHasAccessToCourse(HashMap<String,Object> data) {
        Long courseId = longFromField("courseId",data);
        Course course = courseRepository.read(courseId);
        if (course == null) {
            return false;
        }

        UserInfo userInfo = userInfoRepository.getCurrentUser(data);
        if (userInfo == null) {
            return false;
        }
        return isUserHasAccessToCourse(userInfo,course);
    }

    //InnerClass
    public class TutorialInternalInfo {
        public String name;
        public Long duration = null;
    }

    @Action(name = "getTutorialsForCourse" , mandatoryFields = {"courseId"})
    @Transactional
    public HashMap<Integer,TutorialInternalInfo> getTutorialsForCourse(HashMap<String , Object> data) {
        Long courseId = new Long(longFromField("courseId",data));
        Course course = courseRepository.read(courseId);
        if (course == null) {
            return null;
        }
        List<Tutorial> tutorials = course.getTutorials();
        HashMap<Integer,TutorialInternalInfo> result = new HashMap<>();
        for (Tutorial tutorial : tutorials) {
            TutorialInternalInfo internalInfo = new TutorialInternalInfo();
            internalInfo.name = tutorial.getName();
            if (tutorial.getVideo() != null) {
                internalInfo.duration = tutorial.getVideo().getDuration();
            }
            result.put(tutorial.getOrderNumber(),internalInfo);
        }
        return result;
    }

    @Action(name = "startCourse" , mandatoryFields = {"courseId"})
    @Transactional
    public Result startCourse(HashMap<String , Object> data) {
        Long courseId = new Long(longFromField("courseId", data));
        Course course = courseRepository.read(courseId);
        if (course == null) return Result.NotFound();

        UserInfo userInfo = userInfoRepository.getCurrentUser(data);
        if (userInfo == null) return Result.NotAuthorized();
        if (isUserHasAccessToCourse(userInfo,course)) return Result.AccessDenied();

        userInfoRepository.startCourse(userInfo,course);
        return Result.Complete();
    }

    @Override
    public byte[] getImageById(long id) {
        Course course = courseRepository.read(id);
        return course == null ? null : course.getCover();
    }
}
