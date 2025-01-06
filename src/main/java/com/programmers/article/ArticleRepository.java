package com.programmers.article;

import com.programmers.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Article findByQuestion(Question question);
}
