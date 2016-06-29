package com.getknowledge.modules.courses.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.Course;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.modules.folder.Folder;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("courses")
@ModuleInfo(repositoryName = "GroupCoursesRepository", serviceName = "FolderService")
public class GroupCourses extends Folder {

    @Transient
    private long coursesCount = 0;

    @OneToMany(mappedBy = "groupCourses")
    @JsonIgnore
    private List<Course> courses;

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public long getCoursesCount() {
        return coursesCount;
    }

    public void setCoursesCount(long coursesCount) {
        this.coursesCount = coursesCount;
    }

    @Override
    public Folder cloneFolder() {
        GroupCourses groupCourses = new GroupCourses();
        groupCourses.setCoursesCount(getCoursesCount());
        return groupCourses;
    }
}
