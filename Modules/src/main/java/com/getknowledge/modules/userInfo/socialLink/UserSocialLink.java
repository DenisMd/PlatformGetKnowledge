package com.getknowledge.modules.userInfo.socialLink;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserSocialLink {
    @Column(name = "vk_link" , length = 500)
    private String vkLink;

    @Column(name = "github_link", length = 500)
    private String githubLink;

    @Column(name = "twitterLink" , length = 500)
    private String twitterLink;

    @Column(name = "facebook_link" , length = 500)
    private String facebookLink;

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getGithubLink() {
        return githubLink;
    }

    public void setGithubLink(String githubLink) {
        this.githubLink = githubLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public String getVkLink() {
        return vkLink;
    }

    public void setVkLink(String vkLink) {
        this.vkLink = vkLink;
    }
}
