package com.programmers.answer;

import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.QQuestion;
import com.programmers.recommend.answerRecommend.QARecommend;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class AnswerQuerydsl extends QuerydslRepositorySupport {
    private final QAnswer a = QAnswer.answer;
    private final QQuestion q = QQuestion.question;
    private final QARecommend rec = QARecommend.aRecommend;

    public AnswerQuerydsl() {
        super(Answer.class);
    }

    public Page<Answer> getAnswerPage(Long questionId, PageRequestDto pageRequestDto) {
        return Page.empty();
    }

    private List<Answer> getAnswerList(Long questionId, PageRequestDto pageRequestDto) {
        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifierList(pageRequestDto);
        return from(a)
                .join(q)
                .on(a.question.eq(q))
                .where(q.id.eq(questionId))
                .offset(1)
                .limit(1)
                .fetch();
    }

    private List<OrderSpecifier<?>> getOrderSpecifierList(PageRequestDto pageRequestDto) {
        Map<String, Boolean> filters = pageRequestDto.filters();
        if (filters.isEmpty()){
            return List.of();
        }
        List<OrderSpecifier<?>> orderSpecifierList = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : filters.entrySet()) {
            String key = entry.getKey();
            Boolean desc = entry.getValue();
            Order orderDirect = Order.ASC;
            if (!desc) {
                orderDirect = Order.DESC;
            }
            orderSpecifierList.add(new OrderSpecifier<>(orderDirect, Expressions.stringTemplate("answer" + "." + key)));
        }
        return orderSpecifierList;
    }
}
