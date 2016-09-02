package com.getknowledge.modules.courses.tutorial.comments.review;

import com.getknowledge.modules.messages.comments.CommentRepository;
import org.springframework.stereotype.Repository;

@Repository("TutorialReviewRepository")
public class TutorialReviewRepository extends CommentRepository<TutorialReview> {
    @Override
    protected Class<TutorialReview> getClassEntity() {
        return TutorialReview.class;
    }

    @Override
    protected String getEntityName() {
        return TutorialReview.class.getSimpleName();
    }
}
