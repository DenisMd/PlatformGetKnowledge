package com.getknowledge.modules.courses.tutorial.test.question.answer;

import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.stereotype.Repository;

@Repository("AnswerRepository")
public class AnswerRepository extends ProtectedRepository<Answer> {
    @Override
    protected Class<Answer> getClassEntity() {
        return Answer.class;
    }
}
