package baekgwa.sbb.domain.question.service;

import baekgwa.sbb.model.answer.entity.Answer;
import baekgwa.sbb.model.question.entity.Question;
import baekgwa.sbb.model.user.entity.SiteUser;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public enum QuestionSpecificationBuilder {
    INSTANCE;

    public Specification<Question> searchByKeywordAndCategoryType(String keyword, String categoryType) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Question, SiteUser> authorJoin = root.join("siteUser", JoinType.LEFT);
            Join<Question, Answer> answerJoin = root.join("answerList", JoinType.LEFT);
            Join<Answer, SiteUser> answerAuthorJoin = answerJoin.join("siteUser", JoinType.LEFT);

            Predicate keywordPredicate = cb.or(
                    cb.like(root.get("subject"), "%" + keyword + "%"),         // 제목
                    cb.like(root.get("content"), "%" + keyword + "%"),         // 내용
                    cb.like(authorJoin.get("username"), "%" + keyword + "%"),  // 질문 작성자
                    cb.like(answerJoin.get("content"), "%" + keyword + "%"),   // 답변 내용
                    cb.like(answerAuthorJoin.get("username"), "%" + keyword + "%") // 답변 작성자
            );

            Predicate categoryPredicate = cb.equal(root.get("category").get("categoryType"), categoryType);

            return cb.and(keywordPredicate, categoryPredicate);
        };
    }
}