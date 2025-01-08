package com.programmers.answer;

import com.programmers.answer.dto.AnswerModifyRequestDto;
import com.programmers.answer.dto.AnswerRegisterRequestDto;
import com.programmers.article.Article;
import com.programmers.article.ArticleRepository;
import com.programmers.exception.IdMismatchException;
import com.programmers.exception.NotFoundDataException;
import com.programmers.page.PageableUtils;
import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.Question;
import com.programmers.question.QuestionRepository;
import com.programmers.user.SiteUser;
import com.programmers.user.SiteUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    public Answer createAnswer(Long questionId, AnswerRegisterRequestDto requestDto, String username) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new NotFoundDataException("Question not found"));
        SiteUser siteUser = siteUserRepository.findByUsername(username).orElseThrow(() -> new NotFoundDataException("User not found"));

        Article article = articleRepository.save(Article.builder()
                .siteUser(siteUser)
                .build());

        return answerRepository.save(Answer.builder()
                .article(article)
                .question(question)
                .content(requestDto.content())
                .build());
    }

    public void modifyAnswer(Long questionId, Long answerId, String username, AnswerModifyRequestDto requestDto) {
        //쿼리문 많아서 dsl로 바꾸는 게 좋을 예정
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new NotFoundDataException("Question not found"));
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new NotFoundDataException("Answer not found"));
        SiteUser siteUser = siteUserRepository.findByUsername(username).orElseThrow(() -> new NotFoundDataException("User not found"));

        if (!answer.getArticle().getSiteUser().equals(siteUser)) {
            throw new IdMismatchException("answer id mismatch");
        }
        if (!answer.getQuestion().equals(question)) {
            throw new IdMismatchException("answer question mismatch");
        }

        answer.setContent(requestDto.content());
    }

    public Page<Answer> getAnswers(Long questionId, PageRequestDto pageRequestDto) {
        return answerQuerydsl.getAnswerPage(questionId, pageRequestDto);
//        return answerRepository.findByQuestion(question, PageableUtils.createPageable(pageRequestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT_FILED));
    }
}
