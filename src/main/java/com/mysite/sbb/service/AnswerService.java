package com.mysite.sbb.service;

import com.mysite.sbb.domain.answer.entity.Answer;
import com.mysite.sbb.domain.question.entity.Question;
import com.mysite.sbb.domain.user.entity.SiteUser;

public interface AnswerService {
    void create(Question question, String content);

    Answer create(Question question, String content, SiteUser author);

    Answer getAnswer(Integer id);

    void modify(Answer answer, String content);

    void delete(Answer answer);

    void vote(Answer answer, SiteUser siteUser);
}
