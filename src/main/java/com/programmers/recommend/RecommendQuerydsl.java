package com.programmers.recommend;

import com.programmers.answer.QAnswer;
import com.programmers.article.Article;
import com.programmers.article.QArticle;
import com.programmers.question.QQuestion;
import com.programmers.user.QSiteUser;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Slf4j
@Transactional
public class RecommendQuerydsl extends QuerydslRepositorySupport {
    private final QQuestion q = QQuestion.question;
    private final QAnswer a = QAnswer.answer;
    private final QArticle article = QArticle.article;
    private final QSiteUser user = QSiteUser.siteUser;
    private final QRecommend r = QRecommend.recommend;
    private JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public RecommendQuerydsl() {
        super(Recommend.class);
    }

    private JPQLQuery<Tuple> selectArticleQuery(){
        return JPAExpressions
                .select(user, article, Expressions.currentTimestamp())
                .from(user)
                .innerJoin(article)
                .on(article.siteUser.eq(user));
    }
    public void saveQuestionRecommend(String username, Long questionId){
        queryFactory.insert(r)
                .columns(r.siteUser, r.article, r.createDate) // JPA 어노테이션(@CreatedDate) 무시됨. 직접 insert 해야 함.
                .select(
                        selectArticleQuery()
                            .innerJoin(q)
                            .on(q.article.eq(article))
                            .where(user.username.eq(username).and(q.id.eq(questionId)))
                ) //values 를 하면 select 서브 쿼리 시 오류가 남. https://kongpowder.tistory.com/25
                .execute();
    }

    public void saveAnswerRecommend(String username, Long answerId){
        queryFactory.insert(r)
                .columns(r.siteUser, r.article, r.createDate)
                .select(
                        selectArticleQuery()
                                .innerJoin(a)
                                .on(a.article.eq(article))
                                .where(user.username.eq(username).and(a.id.eq(answerId)))
                )
                .execute();
    }

    public Optional<Article> findArticleByQuestionId(Long questionId) {
        return Optional.ofNullable(
                from(article)
                        .innerJoin(q)
                        .on(q.article.eq(article))
                        .where(q.id.eq(questionId))
                .fetchOne());
    }

    public boolean existsRecommendByQuestionId(String username, Long questionId) {
        long count = from(r)
                .innerJoin(article)
                .on(r.article.eq(article))
                .innerJoin(q)
                .on(q.article.eq(article))
                .innerJoin(user)
                .on(article.siteUser.eq(user))
                .where(q.id.eq(questionId)
                        .and(user.username.eq(username)))
                .fetchCount();

        return count > 0;
    }
}
