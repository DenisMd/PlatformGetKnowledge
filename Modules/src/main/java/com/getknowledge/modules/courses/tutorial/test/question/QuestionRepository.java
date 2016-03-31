package com.getknowledge.modules.courses.tutorial.test.question;

import com.getknowledge.modules.courses.tutorial.test.question.answer.Answer;
import com.getknowledge.modules.courses.tutorial.test.question.answer.AnswerRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("QuestionRepository")
public class QuestionRepository extends ProtectedRepository<Question> {

    @Autowired
    private AnswerRepository answerRepository;

    @Override
    protected Class<Question> getClassEntity() {
        return Question.class;
    }

    @Override
    public void remove(Question entity) {
        for (Answer answer : entity.getAnswerList()) {
            answerRepository.remove(answer);
        }
        super.remove(entity);
    }
}
