package com.getknowledge.modules.courses;

import com.getknowledge.modules.courses.group.GroupCourses;
import com.getknowledge.modules.courses.group.GroupCoursesRepository;
import com.getknowledge.modules.courses.tags.CoursesTag;
import com.getknowledge.modules.courses.tags.CoursesTagRepository;
import com.getknowledge.modules.courses.version.Version;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.dictionaries.language.LanguageRepository;
import com.getknowledge.modules.dictionaries.language.names.Languages;
import com.getknowledge.modules.programs.Program;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private LanguageRepository languageRepository;

    @Autowired
    private CoursesTagRepository coursesTagRepository;

    private void prepareTag(HashMap<String,Object> data , Course course) {
        if (data.containsKey("tags")) {
            List<String> tags = (List<String>) data.get("tags");
            for (String tag : tags) {
                CoursesTag coursesTag = coursesTagRepository.createIfNotExist(tag);
                course.getTags().add(coursesTag);
            }
        }
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

        Result result = Result.Complete();
        result.setObject(course);
        return result;
    }

    @Action(name = "createCourse" , mandatoryFields = {"name","groupCourseId","description","language"})
    public Result createProgram(HashMap<String,Object> data) {
        if (!data.containsKey("principalName"))
            return Result.NotAuthorized();

        UserInfo userInfo = userInfoService.getAuthorizedUser(data);

        Course course = new Course();
        course.setAuthor(userInfo);

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

        courseRepository.create(course);

        for (CoursesTag coursesTag : course.getTags()) {
            coursesTag.getCourses().add(course);
            coursesTagRepository.merge(coursesTag);
        }
        Result result = Result.Complete();
        result.setObject(course.getId());
        return result;
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

    public boolean isUserHasAccessToCourse(UserInfo userInfo , Course course) {
        if (course.getBase()) {
            return true;
        }

        for (Course userCourse : userInfo.getCourses()) {
            if (userCourse != null && userCourse.equals(course)) {
             return true;
            }
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
