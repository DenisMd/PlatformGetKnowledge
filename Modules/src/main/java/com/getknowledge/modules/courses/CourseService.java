package com.getknowledge.modules.courses;

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
import com.getknowledge.modules.programs.Program;
import com.getknowledge.modules.programs.tags.ProgramTag;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.modules.video.Video;
import com.getknowledge.modules.video.VideoRepository;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@Service("CourseService")
public class CourseService extends AbstractService implements ImageService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private GroupCoursesRepository groupCoursesRepository;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private TraceService trace;

    @Autowired
    private KnowledgeRepository knowledgeRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private CoursesTagRepository coursesTagRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private TutorialRepository tutorialRepository;

    private void prepareTag(HashMap<String,Object> data , Course course) {
        if (data.containsKey("tags")) {
            List<String> tags = (List<String>) data.get("tags");
            for (String tag : tags) {
                CoursesTag coursesTag = coursesTagRepository.createIfNotExist(tag);
                course.getTags().add(coursesTag);
            }
        }
    }

    private void removeTagsFromCourse(Course course) {

        for (CoursesTag coursesTag : course.getTags()) {
            coursesTag.getCourses().remove(course);
            coursesTagRepository.merge(coursesTag);
        }

        course.getTags().clear();
    }

    private Result checkCourseRight(HashMap<String,Object> data) {
        Long courseId = new Long((Integer)data.get("courseId"));
        Course course = courseRepository.read(courseId);
        if (course == null) {
            return Result.NotFound();
        }

        UserInfo userInfo = userInfoService.getAuthorizedUser(data);

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

    @Action(name = "createCourse" , mandatoryFields = {"name","groupCourseId","description","language","base"})
    public Result createProgram(HashMap<String,Object> data) {
        if (!data.containsKey("principalName"))
            return Result.NotAuthorized();

        UserInfo userInfo = userInfoService.getAuthorizedUser(data);

        Course course = new Course();
        course.setAuthor(userInfo);
        course.setCreateDate(Calendar.getInstance());

        if (!course.getAuthorizationList().isAccessCreate(userInfo.getUser())) {
            return Result.AccessDenied();
        }

        Long groupCourseId = new Long((Integer)data.get("groupCourseId"));

        GroupCourses groupCourses =  groupCoursesRepository.read(groupCourseId);
        if (groupCourses == null) {
            trace.log("Group program id is incorrect" , TraceLevel.Warning);
            return Result.Failed();
        }

        course.setGroupCourses(groupCourses);
        course.setName((String) data.get("name"));
        course.setDescription((String) data.get("description"));

        try {
            Language language = languageRepository.getLanguage(Languages.valueOf((String) data.get("language")));
            course.setLanguage(language);
        } catch (Exception exception) {
            Result result = Result.Failed();
            result.setObject("Language not found");
            return result;
        }


        prepareTag(data,course);

        course.setRelease(false);
        course.setVersion(new Version(1,0,0));


        course.setBase((Boolean) data.get("base"));

        courseRepository.create(course);

        for (CoursesTag coursesTag : course.getTags()) {
            coursesTag.getCourses().add(course);
            coursesTagRepository.merge(coursesTag);
        }
        Result result = Result.Complete();
        result.setObject(course.getId());
        return result;
    }

    @Action(name = "updateCourseInformation" , mandatoryFields = {"courseId"})
    public Result updateProgramInformation(HashMap<String,Object> data) {

        Result result = checkCourseRight(data);
        Course course;
        if (result.getObject() != null)  {
            course = (Course) result.getObject();
        } else {
            return result;
        }

        if (data.containsKey("name")) {
            String name = (String) data.get("name");
            course.setName(name);
        }

        if (data.containsKey("description")) {
            String description = (String) data.get("description");
            course.setDescription(description);
        }

        removeTagsFromCourse(course);
        prepareTag(data,course);

        if (data.containsKey("sourceKnowledge")) {
            //Source knowledge задаются только при первой версии
            if (course.getVersion().equals(new Version(1,0,0))) {
                course.getSourceKnowledge().clear();
                List<Integer> ids = (List<Integer>) data.get("sourceKnowledge");
                for (Integer id : ids) {
                    Knowledge knowledge = knowledgeRepository.read(new Long(id));
                    course.getSourceKnowledge().add(knowledge);
                }
            }
        }

        if (data.containsKey("requiredKnowledge")) {
            //Source knowledge задаются только при первой версии и для курсов которые не являются базовыми
            if (course.getVersion().equals(new Version(1,0,0)) && !course.isBase()) {
                course.getRequiredKnowledge().clear();
                List<Integer> ids = (List<Integer>) data.get("requiredKnowledge");
                for (Integer id : ids) {
                    Knowledge knowledge = knowledgeRepository.read(new Long(id));
                    course.getRequiredKnowledge().add(knowledge);
                }
            }
        }

        courseRepository.merge(course);
        for (CoursesTag coursesTag : course.getTags()) {
            coursesTag.getCourses().add(course);
            coursesTagRepository.merge(coursesTag);
        }

        coursesTagRepository.removeUnusedTags();

        return Result.Complete();
    }

    @ActionWithFile(name = "uploadCover" , mandatoryFields = "courseId")
    public Result updataCover(HashMap<String,Object> data, List<MultipartFile> files) {
        Result result = checkCourseRight(data);
        Course course;
        if (result.getObject() != null)  {
            course = (Course) result.getObject();
        } else {
            return result;
        }

        try {
            course.setCover(files.get(0).getBytes());
        } catch (IOException e) {
            trace.logException("Error read cover for program" , e, TraceLevel.Warning);
            return Result.Failed();
        }

        courseRepository.merge(course);
        return Result.Complete();
    }

    @ActionWithFile(name = "uploadVideoIntro" , mandatoryFields = "courseId")
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
                Video intro = new Video();
                intro.setAllowEveryOne(true);
                intro.setVideoName((String) data.get("videoName"));
                intro.setCover(files.get(0).getBytes());
                videoRepository.create(intro);
                course.setIntro(intro);
                courseRepository.merge(course);
            } else {
                Video intro = course.getIntro();
                intro.setVideoName((String) data.get("videoName"));
                intro.setCover(files.get(0).getBytes());
                videoRepository.merge(intro);
            }

        } catch (IOException e) {
            trace.logException("Error read cover for program" , e, TraceLevel.Warning);
            return Result.Failed();
        }

        courseRepository.merge(course);
        return Result.Complete();
    }

    @Action(name = "createTutorial" , mandatoryFields = {"courseId","name"})
    public Result createTutorial(HashMap<String , Object> data) {
        Result result = checkCourseRight(data);
        Course course;
        if (result.getObject() != null)  {
            course = (Course) result.getObject();
        } else {
            return result;
        }

        Tutorial tutorial = new Tutorial();
        tutorial.setName((String) data.get("name"));
        tutorial.setCourse(course);
        Object maxOrder = entityManager.createQuery("select max(t.orderNumber) from Tutorial t where t.course.id = :id")
                .setParameter("id" , course.getId()).getSingleResult();

        tutorial.setOrderNumber(maxOrder == null ? 1 : ((Integer) maxOrder) + 1);
        tutorialRepository.create(tutorial);

        Result result1 = Result.Complete();
        result1.setObject(tutorial.getId());
        return result1;
    }

    @Action(name = "getTutorialsForCourse" , mandatoryFields = {"courseId"})
    @Transactional
    public HashMap<Integer,String> getTutorialsForCourse(HashMap<String , Object> data) {
        Long courseId = new Long((Integer)(data.get("courseId")));
        Course course = courseRepository.read(courseId);
        if (course == null) {
            return null;
        }
        UserInfo currentUser = userInfoService.getAuthorizedUser(data);
        if (!isUserHasAccessToCourse(currentUser , course)) {
            return null;
        }
        List<Tutorial> tutorials = course.getTutorials();
        HashMap<Integer,String> result = new HashMap<>();
        for (Tutorial tutorial : tutorials) {
            result.put(tutorial.getOrderNumber(),tutorial.getName());
        }
        return result;
    }

    public boolean isUserHasAccessToCourse(UserInfo userInfo , Course course) {
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

    @Override
    public byte[] getImageById(long id) {
        Course course = courseRepository.read(id);
        if (course != null) {
            return course.getCover();
        } else {
            return  null;
        }
    }
}
