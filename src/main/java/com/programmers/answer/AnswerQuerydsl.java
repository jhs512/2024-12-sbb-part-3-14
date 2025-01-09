package com.programmers.answer;

import com.programmers.answer.dto.AnswerViewDto;
import com.programmers.article.QArticle;
import com.programmers.comment.QComment;
import com.programmers.question.QQuestion;
import com.programmers.recommend.QRecommend;
import com.programmers.user.QSiteUser;
import com.programmers.util.QuerydslUtils;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@Slf4j
public class AnswerQuerydsl extends QuerydslRepositorySupport {
    private final QAnswer a = QAnswer.answer;
    private final QQuestion q = QQuestion.question;
    private final QArticle article = QArticle.article;
    private final QRecommend r = QRecommend.recommend;
    private final QSiteUser u = QSiteUser.siteUser;
    private final QComment c = QComment.comment;

    private static final String DEFAULT_SORT_FILED = "id";

    public AnswerQuerydsl() {
        super(Answer.class);
    }

    public Page<AnswerViewDto> getAnswerPage(Long questionId, Pageable pageable) {
        long totalElements = from(a)
                        .innerJoin(q)
                        .on(a.question.eq(q))
                        .where(q.id.eq(questionId))
                        .fetchCount();
        List<AnswerViewDto> content = getAnswerList(questionId, pageable);
        return new PageImpl<>(content, pageable, totalElements);
    }


    private List<AnswerViewDto> getAnswerList(Long questionId, Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = QuerydslUtils.getOrderSpecifierList(pageable.getSort(), "answer",  a.article, DEFAULT_SORT_FILED);
        return from(a)
                .innerJoin(q)
                .on(a.question.eq(q))
                .innerJoin(article)
                .on(a.article.eq(article))
                .innerJoin(u)
                .on(article.siteUser.eq(u))
                .leftJoin(r)
                .on(r.article.eq(article))
                .leftJoin(c)
                .on(c.targetArticle.eq(article))
                .where(q.id.eq(questionId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .groupBy(a.id)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier<?>[0]))
                .select(Projections.constructor(AnswerViewDto.class,
                        a.id,
                        a.content,
                        u.username,
                        c.id.count(),
                        r.id.count(),
                        article.createdAt,
                        a.lastModifiedAt
                        ))
                .fetch();
    }

//    private List<OrderSpecifier<?>> getOrderSpecifierList(Sort sort, String entityName, String defaultSortFiled) {
//        List<OrderSpecifier<?>> orderSpecifierList = new ArrayList<>();
//
//        sort.forEach(order -> {
//            String property = order.getProperty();
//            Order orderDirect = order.isDescending() ? Order.DESC : Order.ASC;
//            if (property.equals("recommendation")) {
//                Expression<Long> recommendationCount =recommendationCountQuery();
//                orderSpecifierList.add(new OrderSpecifier<>(Order.DESC , recommendationCount));
//            }else {
//                orderSpecifierList.add(new OrderSpecifier<>(orderDirect, Expressions.stringTemplate(entityName + "." + property)));
//            }
//        });
//        orderSpecifierList.add(new OrderSpecifier<>(Order.DESC, Expressions.stringTemplate(entityName + "." + defaultSortFiled)));
//        return orderSpecifierList;
//    }
//
//    private Expression<Long> recommendationCountQuery(){
//        return JPAExpressions
//                .select(r.count())
//                .from(r)
//                .leftJoin(article)
//                .on(r.article.eq(article))
//                .where(article.eq(a.article));
//    }

    public Optional<Answer> getAnswer(Long questionId, Long answerId, String username) {
        return Optional.ofNullable(
                from(a)
                        .select(a)
                        .innerJoin(q)
                        .on(a.question.eq(q))
                        .innerJoin(article)
                        .on(a.article.eq(article))
                        .innerJoin(u)
                        .on(article.siteUser.eq(u))
                        .where(q.id.eq(questionId)
                                .and(a.id.eq(answerId))
                                .and(u.username.eq(username)))
                        .fetchOne()
        );
    }
}
