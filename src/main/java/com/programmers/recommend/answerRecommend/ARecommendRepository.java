package com.programmers.recommend.answerRecommend;

import com.programmers.answer.Answer;
import com.programmers.question.Question;
import com.programmers.recommend.questionRecommend.QRecommend;
import com.programmers.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ARecommendRepository extends JpaRepository<ARecommend, Long> {
    boolean existsByAnswerAndSiteUser(Answer answer, SiteUser siteUser);
}
