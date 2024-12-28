package com.programmers.recommend.questionRecommend;

import com.programmers.question.Question;
import com.programmers.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QRecommendRepository extends JpaRepository<QRecommend, Long> {
    boolean existsByQuestionAndSiteUser(Question question, SiteUser siteUser);
}
