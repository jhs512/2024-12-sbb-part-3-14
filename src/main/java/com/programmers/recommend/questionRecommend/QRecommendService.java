package com.programmers.recommend.questionRecommend;

import com.programmers.exception.AlreadyRecommendedException;
import com.programmers.exception.NotFoundDataException;
import com.programmers.question.Question;
import com.programmers.question.QuestionRepository;
import com.programmers.user.SiteUser;
import com.programmers.user.SiteUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class QRecommendService {
    private final com.programmers.recommend.questionRecommend.QRecommendRepository QRecommendRepository;
    private final SiteUserRepository siteUserRepository;
    private final QuestionRepository questionRepository;

    public void recommend(Long questionId, String username) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new NotFoundDataException("Question not found"));
        SiteUser siteUser = siteUserRepository.findByUsername(username).orElseThrow(() -> new NotFoundDataException("User not found"));

        if(QRecommendRepository.existsByQuestionAndSiteUser(question, siteUser)) {
            throw new AlreadyRecommendedException();
        }
        QRecommendRepository.save(QRecommend.builder()
                        .question(question)
                        .siteUser(siteUser)
                .build());
    }
}
