package com.getknowledge.modules.tags;

import java.util.List;

/**
 * Created by dmarkov on 6/29/2016.
 */
public interface EntityWithTag<T extends Tag> {
    public List<T> getTags();
}
