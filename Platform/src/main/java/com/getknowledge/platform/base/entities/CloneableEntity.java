package com.getknowledge.platform.base.entities;

import com.getknowledge.platform.base.entities.AbstractEntity;

public interface CloneableEntity<T extends AbstractEntity>{
    T clone ();
}
