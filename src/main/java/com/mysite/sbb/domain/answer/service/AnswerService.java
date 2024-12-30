package com.mysite.sbb.domain.answer.service;

import com.mysite.sbb.domain.answer.doamin.Answer;
import com.mysite.sbb.domain.question.domain.Question;
import com.mysite.sbb.domain.user.domain.SiteUser;

public interface AnswerService {

    Object getList(int page, String kw);

    Answer create(Question question, String content, SiteUser author);

    Answer getAnswer(Integer id);

    void modify(Answer answer, String content);

    void delete(Answer answer);

    void vote(Answer answer, SiteUser siteUser);
}
