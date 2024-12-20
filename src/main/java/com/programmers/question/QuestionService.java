package com.programmers.question;


import com.programmers.answer.Answer;
import com.programmers.answer.AnswerRepository;
import com.programmers.exception.NotFoundDataException;
import com.programmers.page.PageableUtils;
import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.dto.QuestionModifyRequestDto;
import com.programmers.question.dto.QuestionRegisterRequestDto;
import com.programmers.user.SiteUser;
import com.programmers.user.SiteUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final SiteUserRepository siteUserRepository;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final String DEFAULT_SORT_FILED = "id";

    public void siteUserCheck(Long questionId, String username) {
        Question question = findQuestionById(questionId); // 이미 존재하는 메서드
        SiteUser siteUser = question.getSiteUser();

        if (!username.equals(siteUser.getUsername())) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403));
        }
    }

    public Question createQuestion(QuestionRegisterRequestDto requestDto, Principal principal) {
        SiteUser siteUser = siteUserRepository.findByUsername(principal.getName()).orElseThrow(() -> new NotFoundDataException("User not found"));
        return questionRepository.save(
                Question.builder()
                        .siteUser(siteUser)
                        .subject(requestDto.subject())
                        .content(requestDto.content())
                        .build()
        );
    }

    public Page<Question> findAllQuestions(PageRequestDto requestDto) {
        Page<Question> questionPage = PageableUtils.getPage(questionRepository, requestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT_FILED);

        // 각 질문에 대한 답변 수를 계산하여 추가
        questionPage.getContent().forEach(question -> {
            long answerCount = answerRepository.countByQuestion(question); // 답변의 갯수 계산
            question.setAnswerCount(answerCount); // 답변의 갯수를 Question 객체에 설정 (setter를 통해)
        });
        return questionPage;
    }

    public Question findQuestionById(Long questionId) {
        return questionRepository.findById(questionId).orElseThrow(() -> new NotFoundDataException("Question not found"));
    }

    public void modifyQuestion(Long questionId, String username, QuestionModifyRequestDto requestDto) {
        Question question = findQuestionById(questionId);
        siteUserCheck(questionId, username);

        question.setSubject(requestDto.subject());
        question.setContent(requestDto.content());
        questionRepository.save(question);
    }

    public void deleteQuestion(Long questionId, String username) {
        siteUserCheck(questionId, username);
        Question question = findQuestionById(questionId);
        questionRepository.delete(question);
    }
}
