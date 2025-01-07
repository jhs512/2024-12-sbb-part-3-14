package com.programmers.recommend;

import com.programmers.answer.QAnswer;
import com.programmers.article.QArticle;
import com.programmers.comment.QComment;
import com.programmers.question.QQuestion;
import com.programmers.user.QSiteUser;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
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

@Repository
@Slf4j
@Transactional
public class RecommendQuerydsl extends QuerydslRepositorySupport {
    private final QQuestion q = QQuestion.question;
    private final QAnswer a = QAnswer.answer;
    private final QArticle article = QArticle.article;
    private final QSiteUser user = QSiteUser.siteUser;
    private final QRecommend r = QRecommend.recommend;
    private final QComment c = QComment.comment;

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

    private void saveRecommend(String username, Long id, EntityPathBase<?> target, QArticle qArticle, NumberPath<Long> idPath) {
        queryFactory.insert(r)
                .columns(r.siteUser, r.article, r.createDate)
                .select(
                        JPAExpressions
                                .select(user, article, Expressions.currentTimestamp()) // JPA 어노테이션(@CreatedDate) 무시됨. 직접 insert 해야 함.
                                .from(user)
                                .innerJoin(article)
                                .on(article.siteUser.eq(user))
                                .innerJoin(target)
                                .on(qArticle.eq(article))
                                .where(user.username.eq(username).and(idPath.eq(id))) // ID 조건
                )//values 를 하면 select 서브 쿼리 시 오류가 남. https://kongpowder.tistory.com/25
                .execute();
    }
    public void saveQuestionRecommend(String username, Long questionId){
        saveRecommend(username, questionId, q, q.article, q.id);
    }

    public void saveAnswerRecommend(String username, Long answerId){
        saveRecommend(username, answerId, a, a.article, a.id);
    }

    public void saveCommentRecommend(String username, Long commentId){
        saveRecommend(username, commentId, c, c.parentArticle, c.id);
    }

    private Expression<Long> recommendationCountExpression(Long id, EntityPathBase<?> target, QArticle qArticle, NumberPath<Long> idPath){
        return JPAExpressions
                .select(r.count())
                .from(r)
                .leftJoin(article)
                .on(article.eq(r.article))
                .leftJoin(target)
                .on(qArticle.eq(article))
                .where(idPath.eq(id));
    }

    private JPQLQuery<Long> recommendationCountQuery(Long id, EntityPathBase<?> target, QArticle qArticle, NumberPath<Long> idPath){
        log.info("query called");
        JPQLQuery<Long> result = from(r)
                .select(r.count())
                        .innerJoin(article)
                                .on(r.article.eq(article))
                                        .innerJoin(target)
                                                .on(qArticle.eq(article))
                                                        .where(idPath.eq(id));
        log.info("query done");
        return result;
    }

    public long recommendationCountByQuestion(Long questionId) {
        return recommendationCountQuery(questionId, q, q.article, q.id)
                .fetchCount();
    }

    public long recommendationCountByAnswer(Long answerId) {
        return recommendationCountQuery(answerId, a, a.article, a.id)
                .fetchCount();
    }

    public long recommendationCountByComment(Long commentId) {
        return recommendationCountQuery(commentId, c, c.parentArticle, c.id)
                .fetchCount();
    }

    private boolean existRecommend(String username, Long id, EntityPathBase<?> target, QArticle qArticle, NumberPath<Long> idPath) {
        log.info("exists called");
        log.info("Generated Query: {}", recommendationCountQuery(id, target, qArticle, idPath).toString());
        long count = recommendationCountQuery(id, target, qArticle, idPath)
                .innerJoin(user)
                .on(article.siteUser.eq(user))
                .where(user.username.eq(username))
                .fetchCount();
        log.info("exists done");
        return count > 0;
//        long count = from(r)
//                .innerJoin(article)
//                .on(r.article.eq(article))
//                .innerJoin(target)
//                .on(qArticle.eq(article))
//                .innerJoin(user)
//                .on(article.siteUser.eq(user))
//                .where(idPath.eq(id)
//                        .and(user.username.eq(username)))
//                .fetchCount();

//        return count > 0;
    }

    public boolean existsRecommendByQuestionId(String username, Long questionId) {
        return existRecommend(username, questionId, q, q.article, q.id);
    }

    public boolean existsRecommendByAnswerId(String username, Long answerId) {
        return existRecommend(username, answerId, a, a.article, a.id);
    }

    public boolean existsRecommendByCommentId(String username, Long commentId) {
        return existRecommend(username, commentId, c, c.parentArticle, c.id);
    }
}
