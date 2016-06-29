package com.getknowledge.modules.courses.tags;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.tags.Tag;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("course")
public class CoursesTag extends Tag {

    @ManyToMany
    @JoinTable(name = "tags_course")
    @JsonIgnore
    private List<Course> courses = new ArrayList<>();

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courseList) {
        this.courses = courseList;
    }
}
