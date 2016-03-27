package com.getknowledge.modules.courses;

import com.getknowledge.modules.courses.changelist.ChangeList;
import com.getknowledge.modules.courses.changelist.ChangeListRepository;
import com.getknowledge.modules.courses.tags.CoursesTagRepository;
import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.courses.tutorial.TutorialRepository;
import com.getknowledge.modules.video.VideoRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.exceptions.DeleteException;
import com.getknowledge.platform.exceptions.PlatformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
}
