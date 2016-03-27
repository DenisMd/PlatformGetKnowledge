package com.getknowledge.platform.base.entities;

import com.getknowledge.platform.modules.user.User;

public interface IOwner {
    public boolean isOwner(User user);
}
