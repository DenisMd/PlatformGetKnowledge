package com.getknowledge.modules.courses.tags;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.Course;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "courses_tag")
public class CoursesTag extends AbstractEntity {

    private String name;

    @ManyToMany
    @JoinTable(name = "tags_course")
    @JsonIgnore
    private List<Course> courseList;

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
