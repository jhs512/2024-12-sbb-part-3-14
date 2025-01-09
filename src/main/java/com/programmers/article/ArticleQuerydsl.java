package com.programmers.article;

import com.programmers.answer.dto.AnswerRegisterRequestDto;
import com.programmers.question.QQuestion;
import com.programmers.user.QSiteUser;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleQuerydsl extends QuerydslRepositorySupport {
    private final QArticle article = QArticle.article;
    private final QQuestion q = QQuestion.question;
    private final QSiteUser u = QSiteUser.siteUser;

    public ArticleQuerydsl() {
        super(Article.class);
    }

    private JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public void saveArticle(String username) {
        queryFactory.insert(article)
                .columns(article.siteUser.id, article.createdAt)
                .select(
                        JPAExpressions
                                .select(u.id, Expressions.currentTimestamp()) // JPA 어노테이션(@CreatedDate) 무시됨. 직접 insert 해야 함.
                                .from(u)
                                .where(u.username.eq(username)) // ID 조건
                )//values 를 하면 select 서브 쿼리 시 오류가 남. https://kongpowder.tistory.com/25
                .execute();
    }
}
