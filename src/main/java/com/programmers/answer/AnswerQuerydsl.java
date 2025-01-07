package com.programmers.answer;

import com.programmers.page.PageableUtils;
import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.QQuestion;
import com.programmers.question.Question;
import com.programmers.recommend.QRecommend;
import com.programmers.recommend.answerRecommend.QARecommend;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
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
import static com.querydsl.core.types.dsl.Wildcard.count;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
@Slf4j
public class AnswerQuerydsl extends QuerydslRepositorySupport {
    private final QAnswer a = QAnswer.answer;
    private final QQuestion q = QQuestion.question;
    private final QRecommend r = QRecommend.recommend;

    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final String DEFAULT_SORT_FILED = "id";

    public AnswerQuerydsl() {
        super(Answer.class);
    }

    public Page<Answer> getAnswerPage(Question question, PageRequestDto pageRequestDto) {
        Pageable pageable = PageableUtils.createPageable(pageRequestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT_FILED);

        long totalElements = from(a)
                        .leftJoin(a.question, q)
                        .on(a.question.id.eq(q.id))
                        .where(q.id.eq(question.getId()))
                        .fetchCount();
        List<Answer> content = getAnswerList(question, pageable);
        return new PageImpl<>(content, pageable, totalElements);
    }

    private List<Answer> getAnswerList(Question question, Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifierList(pageable);
        return from(a)
                .select(a)
                .innerJoin(q)
                .on(a.question.eq(q))
                .where(q.eq(question))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier<?>[0]))
                .fetch();
    }

    private List<OrderSpecifier<?>> getOrderSpecifierList(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifierList = new ArrayList<>();

        pageable.getSort().forEach(order -> {
            String property = order.getProperty();
            Order orderDirect = order.isDescending() ? Order.DESC : Order.ASC;
            if (property.equals("recommendation")) {
                Expression<Long> recommendationCount = recommendationCountQuery();
                orderSpecifierList.add(new OrderSpecifier<>(Order.DESC , recommendationCount));
            }else {
                orderSpecifierList.add(new OrderSpecifier<>(orderDirect, Expressions.stringTemplate("answer" + "." + property)));
            }
        });

        return orderSpecifierList;
    }

    private Expression<Long> recommendationCountQuery(){
        return JPAExpressions
                .select(r.count())
                .from(a)
                .leftJoin(r)
                .on(r.answer.id.eq(a.id))
                .where(r.answer.eq(a));
    }
}
