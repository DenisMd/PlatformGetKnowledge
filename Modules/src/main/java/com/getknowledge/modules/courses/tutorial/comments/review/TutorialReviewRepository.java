package com.getknowledge.modules.courses.tutorial.comments.review;

import com.getknowledge.modules.courses.tutorial.comments.question.TutorialQuestion;
import com.getknowledge.modules.messages.CommentStatus;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("TutorialReviewRepository")
public class TutorialReviewRepository extends BaseRepository<TutorialReview> {
    @Override
    protected Class<TutorialReview> getClassEntity() {
        return TutorialReview.class;
    }

    public void blockComment(TutorialReview tutorialReview, CommentStatus commentStatus) {
        tutorialReview.setMessage(null);
        tutorialReview.setCommentStatus(commentStatus);
        merge(tutorialReview);
    }
}
