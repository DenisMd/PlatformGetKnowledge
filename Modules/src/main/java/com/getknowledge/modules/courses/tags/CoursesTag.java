package com.getknowledge.modules.courses.tags;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.Course;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.ITag;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses_tag")
public class CoursesTag extends AbstractEntity implements ITag {

    @Column(name = "tag_name")
    private String tagName;

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

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
