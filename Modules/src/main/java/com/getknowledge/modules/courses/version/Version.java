package com.getknowledge.modules.courses.version;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class Version {

    @Column(name = "major_version")
    private int majorVersion;

    @Column(name = "middle_version")
    private int middleVersion;

    @Column(name = "minor_version")
    private int minorVersion;

    public Version() {
    }

    public Version(int majorVersion, int middleVersion, int minorVersion) {
        checkVersion(majorVersion);
        checkVersion(middleVersion);
        checkVersion(minorVersion);
        this.majorVersion = majorVersion;
        this.middleVersion = middleVersion;
        this.minorVersion = minorVersion;
    }

    private void checkVersion(int version){
        if (version < 0) {
            throw new IllegalArgumentException("Version number is not correct");
        }
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        checkVersion(majorVersion);
        this.majorVersion = majorVersion;
        this.middleVersion = 0;
        this.minorVersion = 0;
    }

    public int getMiddleVersion() {
        return middleVersion;
    }

    public void setMiddleVersion(int middleVersion) {
        checkVersion(middleVersion);
        this.middleVersion = middleVersion;
        this.minorVersion = 0;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        checkVersion(minorVersion);
        this.minorVersion = minorVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Version version = (Version) o;

        if (getMajorVersion() != version.getMajorVersion()) return false;
        if (getMiddleVersion() != version.getMiddleVersion()) return false;
        return getMinorVersion() == version.getMinorVersion();

    }

    @Override
    public int hashCode() {
        int result = getMajorVersion();
        result = 31 * result + getMiddleVersion();
        result = 31 * result + getMinorVersion();
        return result;
    }
}
