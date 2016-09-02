package com.getknowledge.modules.courses.tutorial;

import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.courses.raiting.Rating;
import com.getknowledge.modules.courses.raiting.RatingRepository;
import com.getknowledge.modules.courses.tutorial.comments.question.TutorialQuestion;
import com.getknowledge.modules.courses.tutorial.comments.review.TutorialReview;
import com.getknowledge.modules.courses.tutorial.homeworks.HomeWork;
import com.getknowledge.modules.courses.tutorial.homeworks.HomeWorkRepository;
import com.getknowledge.modules.courses.tutorial.test.TestRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.post.messages.PostMessage;
import com.getknowledge.modules.video.VideoRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.exceptions.DeleteException;
import com.getknowledge.platform.exceptions.PlatformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Repository("TutorialRepository")
public class TutorialRepository extends ProtectedRepository<Tutorial> {
    @Override
    protected Class<Tutorial> getClassEntity() {
        return Tutorial.class;
    }

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private HomeWorkRepository homeWorkRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public void remove(Tutorial tutorial) {
        if (tutorial.getVideo() != null) {
            videoRepository.remove(tutorial.getVideo().getId());
        }

        for (HomeWork homeWork : tutorial.getHomeWorks()) {
            homeWorkRepository.remove(homeWork);
        }

        if (tutorial.getTest() != null) {
            testRepository.remove(tutorial.getTest());
        }

        super.remove(tutorial);
    }

    public Tutorial createTutorial(Course course , String name) {
        Tutorial tutorial = new Tutorial();
        tutorial.setName(name);
        tutorial.setCourse(course);
        Object maxOrder = entityManager.createQuery("select max(t.orderNumber) from Tutorial t where t.course.id = :id")
                .setParameter("id" , course.getId()).getSingleResult();

        tutorial.setOrderNumber(maxOrder == null ? 1 : ((Integer) maxOrder) + 1);
        tutorial.setLastChangeTime(Calendar.getInstance());

        Rating rating = new Rating();
        ratingRepository.create(rating);
        tutorial.setAvgTutorialRating(rating);

        create(tutorial);
        return tutorial;
    }

    public void changeTutorialsOrder(Course course , List<Integer> tutorialsIds) {

        for (Tutorial tutorial : course.getTutorials()) {
            if (!tutorialsIds.contains(tutorial.getId().intValue())) {
                return;
            }
        }

        List<Tutorial> tutorials = new ArrayList<>();
        int currentIndex = 1;
        for (Integer tutId : tutorialsIds) {
            Tutorial tutorial = read((long) tutId);
            if (tutorial == null) {
                return;
            }

            tutorial.setOrderNumber(currentIndex);
            tutorials.add(tutorial);
            currentIndex++;
        }

        for (Tutorial tutorial : tutorials) {
            merge(tutorial);
        }
    }

    public Tutorial getTutorial(Long courseId,Integer orderNumber) {
        Tutorial tutorial = (Tutorial) entityManager.createQuery("select t from Tutorial t where t.course.id = :courseId and t.orderNumber = :orderNumber")
                .setParameter("courseId", courseId)
                .setParameter("orderNumber", orderNumber).getSingleResult();
        return tutorial;
    }

    public List<TutorialQuestion> getQuestion(Tutorial tutorial, int first, int max) {
        List<TutorialQuestion> messages = entityManager.createQuery("select q from TutorialQuestion q " +
                "where q.tutorial.id = :tutorialId order by q.createTime desc")
                .setParameter("tutorialId", tutorial.getId())
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
        return messages;
    }

    public List<TutorialReview> getReviews(Tutorial tutorial, int first, int max) {
        List<TutorialReview> messages = entityManager.createQuery("select q from TutorialReview q " +
                "where q.tutorial.id = :tutorialId order by q.createTime desc")
                .setParameter("tutorialId", tutorial.getId())
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
        return messages;
    }
}
