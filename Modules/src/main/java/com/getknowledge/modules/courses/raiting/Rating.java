package com.getknowledge.modules.courses.raiting;

import com.getknowledge.platform.annotations.ModelView;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.annotations.ViewType;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.CloneableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "rating")
@ModuleInfo(repositoryName = "RatingRepository" , serviceName = "RatingService")
public class Rating extends AbstractEntity implements CloneableEntity<Rating> {

    @Column(name = "quality_information",nullable = false)
    private int qualityInformation;

    @Column(name = "relevance_information",nullable = false)
    private int relevanceInformation;

    @Column(name = "quality_exercises",nullable = false)
    private int qualityExercises;

    @Column(name = "quality_test",nullable = false)
    private int qualityTest;

    @Column(name = "avg_rating",nullable = false)
    private double avgRating;

    private void checkRating(int val) {
        if (val < 0 || val > 5) {
            throw  new IllegalArgumentException("Rating value is illegal : " + val);
        }
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public double getAvgRating() {
        return avgRating;
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

    @Override
    public Rating clone() {
        Rating rating = new Rating();
        rating.setId(getId());
        rating.setObjectVersion(getObjectVersion());
        rating.setAvgRating(getAvgRating());
        rating.setQualityExercises(getQualityExercises());
        rating.setQualityInformation(getQualityInformation());
        rating.setQualityTest(getQualityTest());
        rating.setRelevanceInformation(getRelevanceInformation());
        return rating;
    }
}
