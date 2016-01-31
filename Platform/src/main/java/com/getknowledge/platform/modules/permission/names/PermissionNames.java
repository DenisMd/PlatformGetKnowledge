package com.getknowledge.platform.modules.permission.names;

public enum PermissionNames {
    EditSections("Edit sections") , EditMenu("Edit menu");

    private String name;

    PermissionNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
