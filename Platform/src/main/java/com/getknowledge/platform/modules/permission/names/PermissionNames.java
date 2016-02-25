package com.getknowledge.platform.modules.permission.names;

public enum PermissionNames {
    EditSections("Edit sections") , EditMenu("Edit menu"), EditSocialLinks("Social Links"),
    EditFolders("Edit folders"), ReadHpMessage("Read help desc message"),EditBooks("Edit book"),CreateBooks("Create books"),
    EditProgrammingDictionaries("Edit programming dictionaries");

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
