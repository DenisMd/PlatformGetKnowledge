package com.getknowledge.modules.courses;

import com.getknowledge.modules.courses.changelist.ChangeList;
import com.getknowledge.modules.courses.changelist.ChangeListRepository;
import com.getknowledge.modules.courses.group.GroupCourses;
import com.getknowledge.modules.courses.tags.CoursesTag;
import com.getknowledge.modules.courses.tags.CoursesTagRepository;
import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.courses.tutorial.TutorialRepository;
import com.getknowledge.modules.courses.version.Version;
import com.getknowledge.modules.dictionaries.knowledge.Knowledge;
import com.getknowledge.modules.dictionaries.knowledge.KnowledgeRepository;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.video.Video;
import com.getknowledge.modules.video.VideoRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.exceptions.DeleteException;
import com.getknowledge.platform.exceptions.PlatformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Repository("CourseRepository")
public class CourseRepository extends ProtectedRepository<Course> {
    @Override
    protected Class<Course> getClassEntity() {
        return Course.class;
    }

    @Autowired
    private TutorialRepository tutorialRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ChangeListRepository changeListRepository;

    @Autowired
    private CoursesTagRepository coursesTagRepository;

    @Autowired
    private KnowledgeRepository knowledgeRepository;

    private void removeCourseInfo(Course course) {
        if (course.getTutorials() != null) {
            for (Tutorial tutorial : course.getTutorials()) {
                tutorialRepository.remove(tutorial.getId());
            }
        }

        if (course.getIntro() != null) {
            videoRepository.remove(course.getIntro().getId());
        }

        if (course.getChangeLists() != null) {
            for (ChangeList changeList : course.getChangeLists()) {
                changeListRepository.read(changeList.getId());
            }
        }

        if (course.getTags() != null) {
            coursesTagRepository.removeTagsFromEntity(course);
            coursesTagRepository.removeUnusedTags();
            merge(course);
        }

    }

    @Override
    public void remove(Course course) {
        //Удаляем черновик
        if (course.getBaseCourse() != null) {
            removeCourseInfo(course);
            super.remove(course);

        } else {
            if (course.getDraftCourse() != null) {
                remove(course.getDraftCourse().getId());
            }

            removeCourseInfo(course);
            super.remove(course);
        }
    }

    private void addCourseToTag(Course course) {
        for (CoursesTag coursesTag : course.getTags()) {
            coursesTag.getCourses().add(course);
            coursesTagRepository.merge(coursesTag);
        }
    }

    public Course createCourse(UserInfo author,GroupCourses groupCourses,String name,String description,Language language,List<String> tags,boolean base) {
        Course course = new Course();
        course.setAuthor(author);
        course.setGroupCourses(groupCourses);
        course.setName(name);
        course.setDescription(description);
        course.setLanguage(language);
        course.setBase(base);
        course.setCreateDate(Calendar.getInstance());
        course.setRelease(false);
        course.setVersion(new Version(1,0,0));
        if (tags != null) {
            coursesTagRepository.createTags(tags,course);
        }
        create(course);
        addCourseToTag(course);
        return course;
    }

    public Course updateCourse(Course course,String name,String description,List<String> tags,List<Integer> sourceKnowledgeIds,List<Integer> requiredKnowledgeIds){
        if (name != null)
            course.setName(name);
        if (description != null)
            course.setDescription(description);
        if (tags != null) {
            coursesTagRepository.removeTagsFromEntity(course);
            coursesTagRepository.createTags(tags,course);
        }

        if (sourceKnowledgeIds != null) {
            for (Integer id : sourceKnowledgeIds) {
                Knowledge knowledge = knowledgeRepository.read(new Long(id));
                course.getSourceKnowledge().add(knowledge);
            }
        }

        if (requiredKnowledgeIds != null) {
            for (Integer id : requiredKnowledgeIds) {
                Knowledge knowledge = knowledgeRepository.read(new Long(id));
                course.getRequiredKnowledge().add(knowledge);
            }
        }

        merge(course);
        addCourseToTag(course);
        coursesTagRepository.removeUnusedTags();

        return course;
    }

    public void releaseBaseCourse(Course course,ChangeList changeList) {
        course.getChangeLists().add(changeList);
        course.setRelease(true);
        merge(course);
    }

    public void mergeVideo (Video to, Video from) {
        to.setCover(from.getCover());
        to.setLink(from.getLink());
        to.setVideoName(from.getVideoName());
        videoRepository.merge(to);
    }

    public void createVideo (Video to, Video from) {
        to.setCover(from.getCover());
        to.setLink(from.getLink());
        to.setVideoName(from.getVideoName());
        videoRepository.create(to);
    }

    public void mergeTutorial (Tutorial to, Tutorial from) {
        to.setData(from.getData());
        to.setName(from.getName());
        to.setOrderNumber(from.getOrderNumber());
        to.setLastChangeTime(from.getLastChangeTime());
        mergeVideo(to.getVideo(), from.getVideo());
    }

    public void createTutorial (Tutorial to, Tutorial from) {
        to.setData(from.getData());
        to.setName(from.getName());
        to.setOrderNumber(from.getOrderNumber());
        to.setLastChangeTime(from.getLastChangeTime());
        createVideo(to.getVideo(), from.getVideo());
        tutorialRepository.create(to);
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
                    tutorialRepository.remove(draftTutorial.getOriginalTutorial().getId());
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
        draft.setName(base.getName());
        draft.setCover(base.getCover());
        draft.setDescription(base.getDescription());

        Video video = new Video();
        createVideo(video,base.getIntro());
        draft.setIntro(video);

        coursesTagRepository.createTags(base.getTags().stream().map(tag -> tag.getTagName()).collect(Collectors.toList()),base);

        for (Tutorial baseTutorial : base.getTutorials()) {
            Tutorial tutorial = new Tutorial();
            tutorial.setVideo(new Video());
            tutorial.setOriginalTutorial(baseTutorial);
            createTutorial(tutorial,baseTutorial);
        }
    }
}
