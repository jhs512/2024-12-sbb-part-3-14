package com.programmers.recommend.answerRecommend;

import com.programmers.answer.Answer;
import com.programmers.answer.AnswerRepository;
import com.programmers.exception.AlreadyRecommendedException;
import com.programmers.exception.IdMismatchException;
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
public class ARecommendService {
    private final ARecommendRepository aRecommendRepository;
    private final QuestionRepository questionRepository;
    private final SiteUserRepository siteUserRepository;
    private final AnswerRepository answerRepository;

    public void recommend(Long questionId, Long answerId, String username) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new NotFoundDataException("Question not found"));
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new NotFoundDataException("Answer not found"));

        if (!answer.getQuestion().equals(question)) {
            throw new IdMismatchException("Answer and Question");
        }

        SiteUser siteUser = siteUserRepository.findByUsername(username).orElseThrow(() -> new NotFoundDataException("User not found"));

        if(aRecommendRepository.existsByAnswerAndSiteUser(answer, siteUser)) {
            throw new AlreadyRecommendedException();
        }
        aRecommendRepository.save(ARecommend.builder()
                .answer(answer)
                .siteUser(siteUser)
                .build());
    }
}
