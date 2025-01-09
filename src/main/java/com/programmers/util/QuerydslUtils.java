package com.programmers.util;

import com.programmers.article.QArticle;
import com.programmers.recommend.QRecommend;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuerydslUtils {
    private static final QRecommend RECOMMEND = QRecommend.recommend;
    private static final QArticle ARTICLE = QArticle.article;

    public static List<OrderSpecifier<?>> getOrderSpecifierList(Sort sort, String entityName, QArticle qArticle,String defaultSortFiled) {
        List<OrderSpecifier<?>> orderSpecifierList = new ArrayList<>();

        sort.forEach(order -> {
            String property = order.getProperty();
            Order orderDirect = order.isDescending() ? Order.DESC : Order.ASC;
            if (property.equals("recommendation")) {
                Expression<Long> recommendationCount =recommendationCountQuery(qArticle);
                orderSpecifierList.add(new OrderSpecifier<>(Order.DESC , recommendationCount));
            }else {
                orderSpecifierList.add(new OrderSpecifier<>(orderDirect, Expressions.stringTemplate(entityName + "." + property)));
            }
        });
        orderSpecifierList.add(new OrderSpecifier<>(Order.DESC, Expressions.stringTemplate(entityName + "." + defaultSortFiled)));
        return orderSpecifierList;
    }

    private static Expression<Long> recommendationCountQuery(QArticle qArticle){
        return JPAExpressions
                .select(RECOMMEND.count())
                .from(RECOMMEND)
                .leftJoin(ARTICLE)
                .on(RECOMMEND.article.eq(ARTICLE))
                .where(ARTICLE.eq(qArticle));
    }
}
