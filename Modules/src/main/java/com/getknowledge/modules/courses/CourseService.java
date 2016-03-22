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
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.modules.video.Video;
import com.getknowledge.modules.video.VideoRepository;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.exceptions.PlatformException;
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
import java.util.stream.Collectors;

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

    @Autowired
    private ChangeListRepository changeListRepository;

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

    public void mergeVideo (Video from, Video to) {
        from.setCover(to.getCover());
        from.setLink(to.getLink());
        from.setVideoName(to.getVideoName());
        videoRepository.merge(from);
    }

    public void mergeTutorial (Tutorial from, Tutorial to) {
        from.setData(to.getData());
        from.setName(to.getName());
        from.setOrderNumber(to.getOrderNumber());
        from.setLastChangeTime(to.getLastChangeTime());
        mergeVideo(from.getVideo(), to.getVideo());
    }

    public void mergeDraft(Course base, Course draft) {

        base.setName(draft.getName());
        base.setCover(draft.getCover());
        base.setDescription(draft.getDescription());

        mergeVideo(base.getIntro() , draft.getIntro());

        coursesTagRepository.removeTagsFromEntity(base);
        coursesTagRepository.createTags(draft.getTags().stream().map(tag -> tag.getTagName()).collect(Collectors.toList()),base);

        for (Tutorial draftTutorial : draft.getTutorials()) {
            if (draftTutorial.getOriginalTutorial() != null) {
                if (!draftTutorial.isDeleting()) {
                    mergeTutorial(draftTutorial.getOriginalTutorial(), draftTutorial);
                    tutorialRepository.merge(draftTutorial.getOriginalTutorial());
                } else {
                    //delete tutorial
                    try {
                        tutorialRepository.remove(draftTutorial.getOriginalTutorial().getId());
                    } catch (PlatformException e) {
                        //Нечего делать - сдаемся
                    }
                }
            } else {
                Tutorial newTutorial = new Tutorial();
                newTutorial.setCourse(base);
                mergeTutorial(newTutorial,draftTutorial);
                //create Tutorial
                tutorialRepository.create(newTutorial);
            }
        }
    }

    public void createDraft(Course base, Course draft) {

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


        if (data.containsKey("tags")) {
            List<String> tags = (List<String>) data.get("tags");
            coursesTagRepository.createTags(tags,course);
        }


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
    @Transactional
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

        if (data.containsKey("tags")) {
            List<String> tags = (List<String>) data.get("tags");
            coursesTagRepository.removeTagsFromEntity(course);
            coursesTagRepository.createTags(tags,course);
        }


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
        tutorial.setLastChangeTime(Calendar.getInstance());
        tutorialRepository.create(tutorial);

        Result result1 = Result.Complete();
        result1.setObject(tutorial.getId());
        return result1;
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
                course.setRelease(true);
                ChangeList changeList = new ChangeList();
                changeList.setCourse(course);
                changeList.setVersion(course.getVersion());
                if (course.getLanguage().getName().equals(Languages.Ru.name())) {
                    changeList.getChangeList().add("Инициализация курса");
                } else {
                    changeList.getChangeList().add("Initialize course");
                }
                changeListRepository.create(changeList);
                course.getChangeLists().add(changeList);
                courseRepository.merge(course);
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
                ChangeList changeList = new ChangeList();
                changeList.setVersion(baseCourse.getVersion());
                changeList.setCourse(baseCourse);
                changeList.setChangeList(changes);
                changeListRepository.create(changeList);
                baseCourse.getChangeLists().add(changeList);
            }

            mergeDraft(baseCourse,course);

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
        createDraft(course,draft);

        courseRepository.create(draft);

        result = Result.Complete();
        result.setObject(draft.getId());
        return result;
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
