package com.getknowledge.modules.books.group;

import com.getknowledge.modules.section.Section;
import com.getknowledge.platform.annotations.ModelView;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.annotations.ViewType;
import com.getknowledge.platform.base.entities.CloneableEntity;
import com.getknowledge.platform.base.entities.Folder;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "books_group")
@ModuleInfo(repositoryName = "GroupBooksRepository" , serviceName = "GroupBooksService")
public class GroupBooks extends Folder implements CloneableEntity<GroupBooks> {

    @ManyToOne(optional = false)
    @ModelView(type = {ViewType.Public})
    private Section section;

    @Transient
    private long booksCount = 0;

    public long getBooksCount() {
        return booksCount;
    }

    public void setBooksCount(long booksCount) {
        this.booksCount = booksCount;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    @Override
    public GroupBooks clone() {
        GroupBooks groupBooks = new GroupBooks();
        groupBooks.setUrl(getUrl());
        groupBooks.setTitle(getTitle());
        groupBooks.setId(getId());
        groupBooks.setObjectVersion(getObjectVersion());
        groupBooks.setSection(getSection());
        groupBooks.setCover(getCover());
        groupBooks.setDescriptionEn(getDescriptionEn());
        groupBooks.setDescriptionRu(getDescriptionRu());
        groupBooks.setBooksCount(getBooksCount());
        groupBooks.setCreateDate(getCreateDate());
        return groupBooks;
    }
}
