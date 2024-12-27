package com.mysite.sbb.domain.answer;

import com.mysite.sbb.domain.question.Question;
import com.mysite.sbb.domain.user.SiteUser;

public interface AnswerService {

    Answer create(Question question, String content, SiteUser author);

    Answer getAnswer(Integer id);

    void modify(Answer answer, String content);

    void delete(Answer answer);

    void vote(Answer answer, SiteUser siteUser);
}
