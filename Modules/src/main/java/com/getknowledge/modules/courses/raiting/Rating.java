package com.getknowledge.modules.courses.raiting;

import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "rating")
@ModuleInfo(repositoryName = "RatingRepository" , serviceName = "RatingService")
public class Rating extends AbstractEntity {

    @Column(name = "quality_information",nullable = false)
    private int qualityInformation;

    @Column(name = "relevance_information",nullable = false)
    private int relevanceInformation;

    @Column(name = "quality_exercises",nullable = false)
    private int qualityExercises;

    @Column(name = "quality_test",nullable = false)
    private int qualityTest;

    private void checkRating(int val) {
        if (val <= 0 || val > 5) {
            throw  new IllegalArgumentException("Rating value is illegal : " + val);
        }
    }

    @Column(name = "avg_rating")
    public double getAvgRating() {
        return (getQualityExercises() + getQualityInformation() + getQualityTest() + getRelevanceInformation()) / 4.0;
    }

    public int getQualityExercises() {
        return qualityExercises;
    }

    public void setQualityExercises(int qualityExercises) {
        checkRating(qualityExercises);
        this.qualityExercises = qualityExercises;
    }

    public int getQualityInformation() {
        return qualityInformation;
    }

    public void setQualityInformation(int qualityInformation) {
        checkRating(qualityInformation);
        this.qualityInformation = qualityInformation;
    }

    public int getQualityTest() {
        return qualityTest;
    }

    public void setQualityTest(int qualityTest) {
        checkRating(qualityTest);
        this.qualityTest = qualityTest;
    }

    public int getRelevanceInformation() {
        return relevanceInformation;
    }

    public void setRelevanceInformation(int relevanceInformation) {
        checkRating(relevanceInformation);
        this.relevanceInformation = relevanceInformation;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
