package com.programmers.question;


import com.programmers.answer.QAnswer;
import com.programmers.article.Article;
import com.programmers.article.ArticleRepository;
import com.programmers.exception.NotFoundDataException;
import com.programmers.page.PageableUtils;
import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.dto.QuestionModifyRequestDto;
import com.programmers.question.dto.QuestionRegisterRequestDto;
import com.programmers.question.dto.QuestionViewDto;
import com.programmers.user.SiteUser;
import com.programmers.user.SiteUserRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuestionQuerydsl questionQuerydsl;
    private final ArticleRepository articleRepository;
    private final SiteUserRepository siteUserRepository;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final String DEFAULT_SORT_FILED = "id";

    private void setBooleanBuilder(BooleanBuilder booleanBuilder, String searchKeyword){
        booleanBuilder
                .or(QQuestion.question.subject.containsIgnoreCase(searchKeyword))
                .or(QQuestion.question.content.containsIgnoreCase(searchKeyword))
                .or(QQuestion.question.article.siteUser.username.containsIgnoreCase(searchKeyword))
                .or(QAnswer.answer.content.containsIgnoreCase(searchKeyword))
                .or(QAnswer.answer.article.siteUser.username.containsIgnoreCase(searchKeyword))
        ;
    }

    public Question createQuestion(QuestionRegisterRequestDto requestDto, String username) {
        SiteUser siteUser = siteUserRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundDataException("User not found"));

        Article article = articleRepository.save(Article.builder()
                .siteUser(siteUser)
                .build());

        return questionRepository.save(
                Question.builder()
                        .article(article)
                        .subject(requestDto.subject())
                        .content(requestDto.content())
                        .build()
        );
    }

    public Page<QuestionViewDto> getQuestionPageBySearch(PageRequestDto requestDto, String searchKeyword) {
        Pageable pageable = PageableUtils.createPageable(requestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT_FILED);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            setBooleanBuilder(booleanBuilder, searchKeyword);
        }
        return questionQuerydsl.getQuestionPageBySearch(pageable, booleanBuilder);
    }

    public QuestionViewDto findQuestionById(Long questionId) {
        return questionQuerydsl.findQuestionById(questionId)
                .orElseThrow(() -> new NotFoundDataException("Question not found"));
    }

    // 수정시 사용함
    // question id 존재하는지 확인 - 없으면 404
    // siteUser 존재하는 지 확인 - 없으면 401
    // 둘이 일치하는 지 확인 - 없으면 403
    public Question findQuestionByIdAndUsername(Long questionId, String username) {
        if (!questionRepository.existsById(questionId)) {
            throw new NotFoundDataException("Question not found");
        }
        if (!siteUserRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(401));
        }
        return questionQuerydsl.findByQuestionIdAndUsername(questionId, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(403)));
    }

    public void modifyQuestion(Long questionId, String username, QuestionModifyRequestDto requestDto) {
        Question question = findQuestionByIdAndUsername(questionId, username);
        question.setSubject(requestDto.subject());
        question.setContent(requestDto.content());
        questionRepository.save(question);
    }

    public void deleteQuestion(Long questionId, String username) {
        Question question = findQuestionByIdAndUsername(questionId, username);
        questionRepository.delete(question);
    }
}
