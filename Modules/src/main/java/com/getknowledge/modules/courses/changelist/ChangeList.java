package com.getknowledge.modules.courses.changelist;

import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.courses.version.Version;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "change_list")
public class ChangeList extends AbstractEntity {

    @Embedded
    private Version version;

    @ElementCollection
    @CollectionTable(name = "chagelist_chnages" , joinColumns = @JoinColumn(name = "change_list"))
    @Column(columnDefinition = "Text" , name = "changes")
    private List<String> changeList = new ArrayList<>();

    @ManyToOne(optional = false)
    private Course course;

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public List<String> getChangeList() {
        return changeList;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setChangeList(List<String> changeList) {
        this.changeList = changeList;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
