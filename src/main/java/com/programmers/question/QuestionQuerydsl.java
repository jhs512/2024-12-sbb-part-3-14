package com.programmers.question;

import com.programmers.answer.QAnswer;
import com.programmers.article.QArticle;
import com.programmers.comment.QComment;
import com.programmers.question.dto.QuestionViewDto;
import com.programmers.recommend.QRecommend;
import com.programmers.user.QSiteUser;
import com.programmers.util.QuerydslUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class QuestionQuerydsl extends QuerydslRepositorySupport {
    private final QArticle article = QArticle.article;
    private final QAnswer a = QAnswer.answer;
    private final QQuestion q = QQuestion.question;
    private final QRecommend r = QRecommend.recommend;
    private final QSiteUser questionUser =  new QSiteUser("questionUser");
    private final QSiteUser answerUser = new QSiteUser("answerUser");
    private final QComment c = QComment.comment;


    private static final String DEFAULT_SORT_FILED = "id";

    public QuestionQuerydsl() {
        super(Question.class);
    }

    public Optional<QuestionViewDto> findQuestionById(Long questionId) {
        return Optional.ofNullable(
                from(q)
                        .innerJoin(article)
                        .on(q.article.eq(article))
                        .innerJoin(questionUser)
                        .on(article.siteUser.eq(questionUser))
                        .leftJoin(a)
                        .on(a.question.eq(q))
                        .leftJoin(r)
                        .on(r.article.eq(article))
                        .leftJoin(c)
                        .on(c.targetArticle.eq(article))
                        .groupBy(q.id)
                        .where(q.id.eq(questionId))
                        .select(Projections.constructor(QuestionViewDto.class,
                                q.id,
                                q.subject,
                                q.content,
                                questionUser.username,
                                a.id.count(),
                                c.id.count(),
                                r.id.count(),
                                article.createdAt,
                                q.lastModifiedAt
                ))
                .fetchOne());
    }

    public Optional<Question> findByQuestionIdAndUsername(Long questionId, String username) {
        return Optional.ofNullable(
                from(q)
                        .select(q)
                        .innerJoin(article)
                        .on(q.article.eq(article))
                        .innerJoin(questionUser)
                        .on(article.siteUser.eq(questionUser))
                        .where(q.id.eq(questionId)
                                .and(questionUser.username.eq(username)))
                        .fetchOne()
        );
    }

    public Page<QuestionViewDto> getQuestionPageBySearch(Pageable pageable, BooleanBuilder booleanBuilder) {
        long totalElements = from(q)
                .select(q.id)
                .innerJoin(article)
                .on(q.article.eq(article))
                .innerJoin(questionUser)
                .on(q.article.siteUser.eq(questionUser))
                .leftJoin(a)
                .on(a.question.eq(q))
                .leftJoin(answerUser)
                .on(a.article.siteUser.eq(answerUser))
                .groupBy(q.id)
                .where(booleanBuilder)
                .fetchCount();
        List<QuestionViewDto> content = getQuestionListBySearch(pageable, booleanBuilder);
        return new PageImpl<>(content, pageable, totalElements);
    }

    private List<QuestionViewDto> getQuestionListBySearch(Pageable pageable, BooleanBuilder booleanBuilder) {
        List<OrderSpecifier<?>> orderSpecifiers = QuerydslUtils.getOrderSpecifierList(pageable.getSort(), "question", q.article ,DEFAULT_SORT_FILED);
        return from(q)
                .innerJoin(article)
                .on(q.article.eq(article))
                .innerJoin(questionUser)
                .on(q.article.siteUser.eq(questionUser))
                .leftJoin(questionUser)
                .on(a.article.siteUser.eq(questionUser))
                .leftJoin(a)
                .on(a.question.eq(q))
                .leftJoin(r)
                .on(r.article.eq(article))
                .leftJoin(c)
                .on(c.targetArticle.eq(article))
                .distinct()
                .groupBy(q.id)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier<?>[0]))
                .where(booleanBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .select(Projections.constructor(QuestionViewDto.class,
                        q.id,
                        q.subject,
                        q.content,
                        questionUser.username,
                        a.id.count(),
                        c.id.count(),
                        r.id.count(),
                        article.createdAt,
                        q.lastModifiedAt
                ))
                .fetch();
    }
}
