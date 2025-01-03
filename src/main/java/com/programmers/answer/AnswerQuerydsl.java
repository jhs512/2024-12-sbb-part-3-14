package com.programmers.answer;

import com.programmers.page.PageableUtils;
import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.QQuestion;
import com.programmers.recommend.answerRecommend.QARecommend;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static com.querydsl.core.types.dsl.Wildcard.count;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class AnswerQuerydsl extends QuerydslRepositorySupport {
    private final QAnswer a = QAnswer.answer;
    private final QQuestion q = QQuestion.question;
    private final QARecommend rec = QARecommend.aRecommend;

    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final String DEFAULT_SORT_FILED = "id";

    public AnswerQuerydsl() {
        super(Answer.class);
    }

    public Page<Answer> getAnswerPage(Long questionId, PageRequestDto pageRequestDto) {
        Pageable pageable = PageableUtils.createPageable(pageRequestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT_FILED);

        Long totalElements = Objects.requireNonNull(from(a)
                .select(count)
                .innerJoin(a.question, q)
                .where(q.id.eq(questionId))
                .fetchOne());

        List<Answer> content = getAnswerList(questionId, pageRequestDto);
        return new PageImpl<>(content, pageable, totalElements);
    }

    private List<Answer> getAnswerList(Long questionId, PageRequestDto pageRequestDto) {

        Pageable pageable = PageableUtils.createPageable(pageRequestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT_FILED);
        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifierList(pageable);
        return from(a)
                .select(a)
                .innerJoin(q)
                .on(a.question.eq(q))
                .where(q.id.eq(questionId))
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
            orderSpecifierList.add(new OrderSpecifier<>(orderDirect, Expressions.stringTemplate("answer" + "." + property)));
        });
        return orderSpecifierList;
    }
}
