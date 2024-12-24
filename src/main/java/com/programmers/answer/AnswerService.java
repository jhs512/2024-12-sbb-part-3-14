package com.programmers.answer;

import com.programmers.answer.dto.AnswerModifyRequestDto;
import com.programmers.answer.dto.AnswerRegisterRequestDto;
import com.programmers.exception.IdMismatchException;
import com.programmers.exception.NotFoundDataException;
import com.programmers.page.PageableUtils;
import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.Question;
import com.programmers.question.QuestionRepository;
import com.programmers.user.SiteUser;
import com.programmers.user.SiteUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final SiteUserRepository siteUserRepository;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final String DEFAULT_SORT_FILED = "id";

    public void createAnswer(Long questionId, AnswerRegisterRequestDto requestDto, String username) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new NotFoundDataException("Question not found"));
        SiteUser siteUser = siteUserRepository.findByUsername(username).orElseThrow(() -> new NotFoundDataException("User not found"));

        answerRepository.save(Answer.builder()
                .siteUser(siteUser)
                .question(question)
                .content(requestDto.content())
                .build());
    }

    public void modifyAnswer(Long questionId, Long answerId, String username, AnswerModifyRequestDto requestDto) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new NotFoundDataException("Question not found"));
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new NotFoundDataException("Answer not found"));
        SiteUser siteUser = siteUserRepository.findByUsername(username).orElseThrow(() -> new NotFoundDataException("User not found"));

        if (!answer.getSiteUser().equals(siteUser)) {
            throw new IdMismatchException("answer id mismatch");
        }
        if (!answer.getQuestion().equals(question)) {
            throw new IdMismatchException("answer question mismatch");
        }

        answer.setContent(requestDto.content());
    }

    public Page<Answer> getAnswers(Question question, PageRequestDto pageRequestDto) {
        return answerRepository.findByQuestion(question, PageableUtils.createPageable(pageRequestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT_FILED));
    }
}
