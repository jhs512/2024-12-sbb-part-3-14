package com.programmers.question;


import com.programmers.exception.NotFoundDataException;
import com.programmers.page.PageableUtils;
import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.dto.QuestionRegisterRequestDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final String DEFAULT_SORT_FILED = "id";

    public Question createQuestion(QuestionRegisterRequestDto requestDto) {
        return questionRepository.save(
                Question.builder()
                        .subject(requestDto.subject())
                        .content(requestDto.content())
                        .build()
        );
    }


    public Page<Question> findAllQuestions(PageRequestDto requestDto) {
        return PageableUtils.getPage(questionRepository, requestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT_FILED);
    }

    public Question findQuestionById(Long questionId) {
        return questionRepository.findById(questionId).orElseThrow(() -> new NotFoundDataException("Question not found"));
    }
}
