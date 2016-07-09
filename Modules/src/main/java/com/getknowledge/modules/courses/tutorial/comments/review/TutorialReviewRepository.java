package com.getknowledge.modules.courses.tutorial.comments.review;

import com.getknowledge.modules.courses.tutorial.comments.question.TutorialQuestion;
import com.getknowledge.modules.messages.CommentRepository;
import com.getknowledge.modules.messages.CommentStatus;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
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
