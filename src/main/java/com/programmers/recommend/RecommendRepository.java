package com.programmers.recommend;

import com.programmers.question.Question;
import com.programmers.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    boolean existsByQuestionAndSiteUser(Question question, SiteUser siteUser);

    Recommend findByQuestionAndSiteUser(Question question, SiteUser siteUser);
}
