package com.getknowledge.modules.courses.tutorial.comments.review;

import com.getknowledge.modules.messages.comments.CommentStatus;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.modules.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service("TutorialReviewService")
public class TutorialReviewService extends AbstractService {

    @Autowired
    private TutorialReviewRepository tutorialReviewRepository;

    @Action(name = "blockQuestion", mandatoryFields = {"reviewId","status"})
    @Transactional
    public Result blockComment(HashMap<String,Object> data){

        Long reviewId = longFromField("reviewId" , data);
        TutorialReview tutorialReview = tutorialReviewRepository.read(reviewId);

        if (tutorialReview == null)
            return Result.NotFound();

        if (!isAccessToEdit(data,tutorialReview)){
            return Result.AccessDenied();
        }

        CommentStatus commentStatus = CommentStatus.valueOf((String) data.get("status"));
        if (commentStatus == CommentStatus.Normal) {
            return Result.Failed();
        }
        tutorialReviewRepository.blockComment(tutorialReview, commentStatus);

        return Result.Complete();
    }
}
