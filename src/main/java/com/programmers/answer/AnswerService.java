package com.programmers.answer;

import com.programmers.answer.dto.AnswerModifyRequestDto;
import com.programmers.answer.dto.AnswerRegisterRequestDto;
import com.programmers.answer.dto.AnswerViewDto;
import com.programmers.article.Article;
import com.programmers.article.ArticleQuerydsl;
import com.programmers.article.ArticleRepository;
import com.programmers.exception.NotFoundDataException;
import com.programmers.page.PageableUtils;
import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.Question;
import com.programmers.question.QuestionQuerydsl;
import com.programmers.question.QuestionRepository;
import com.programmers.user.SiteUser;
import com.programmers.user.SiteUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class AnswerService {
    private final ArticleRepository articleRepository;
    private final AnswerRepository answerRepository;
    private final AnswerQuerydsl answerQuerydsl;
    private final QuestionRepository questionRepository;
    private final SiteUserRepository siteUserRepository;


    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final String DEFAULT_SORT_FILED = "id";

    public void createAnswer(Long questionId, AnswerRegisterRequestDto requestDto, String username) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new NotFoundDataException("Question not found"));
        SiteUser siteUser = siteUserRepository.findByUsername(username).orElseThrow(() -> new NotFoundDataException("User not found"));

        Article article = articleRepository.save(Article.builder()
                .siteUser(siteUser)
                .build());

        answerRepository.save(Answer.builder()
                .article(article)
                .question(question)
                .content(requestDto.content())
                .build());
    }

    public void modifyAnswer(Long questionId, Long answerId, String username, AnswerModifyRequestDto requestDto) {
        Answer answer = answerQuerydsl.getAnswer(questionId, answerId, username)
                .orElseThrow(() -> new NotFoundDataException("Answer not found"));
        answer.setContent(requestDto.content());
    }

    public Page<AnswerViewDto> getAnswers(Long questionId, PageRequestDto pageRequestDto) {
        Pageable pageable = PageableUtils.createPageable(pageRequestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT_FILED);
        return answerQuerydsl.getAnswerPage(questionId, pageable);
    }
}
