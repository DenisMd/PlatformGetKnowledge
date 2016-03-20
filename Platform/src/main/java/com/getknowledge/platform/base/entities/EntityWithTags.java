package com.getknowledge.platform.base.entities;

import java.util.List;

public interface EntityWithTags<T extends ITag> {
    List<T> getTags();
    public void setTags(List<T> tags);
}
