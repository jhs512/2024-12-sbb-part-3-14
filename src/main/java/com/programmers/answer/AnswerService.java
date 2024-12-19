package com.programmers.answer;

import com.programmers.answer.dto.AnswerRegisterRequestDto;
import com.programmers.exception.NotFoundDataException;
import com.programmers.page.PageableUtils;
import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.Question;
import com.programmers.question.QuestionRepository;
import com.programmers.user.SiteUser;
import com.programmers.user.SiteUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final SiteUserRepository siteUserRepository;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final String DEFAULT_SORT_FILED = "id";

    public Answer createAnswer(AnswerRegisterRequestDto requestDto, Principal principal) {
        Question question = questionRepository.findById(requestDto.questionId()).orElseThrow(() -> new NotFoundDataException("Question not found"));
        SiteUser siteUser = siteUserRepository.findByUsername(principal.getName()).orElseThrow(() -> new NotFoundDataException("User not found"));

        return answerRepository.save(Answer.builder()
                .siteUser(siteUser)
                .question(question)
                .content(requestDto.content())
                .build());
    }

    public Page<Answer> getAnswers(PageRequestDto pageRequestDto) {
        return PageableUtils.getPage(answerRepository, pageRequestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT_FILED);
    }
}
