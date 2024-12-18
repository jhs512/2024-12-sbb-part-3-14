package com.mysite.sbb.service;

import com.mysite.sbb.domain.Question;
import com.mysite.sbb.domain.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface QuestionService {
    List<Question> getList();

    Page<Question> getList(int page, String kw);

    Question getQuestion(Integer id);

    void create(String subject, String content, SiteUser user);

    void modify(Question question, String subject, String content);

    void delete(Question question);

    void vote(Question question, SiteUser siteUser);

    Specification<Question> search(String kw);
}
