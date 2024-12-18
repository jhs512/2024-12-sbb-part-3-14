package com.programmers.answer;

import com.programmers.answer.dto.AnswerRegisterRequestDto;
import com.programmers.exception.NotFoundDataException;
import com.programmers.page.PageableUtils;
import com.programmers.page.dto.PageRequestDto;
import com.programmers.question.Question;
import com.programmers.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final String DEFAULT_SORT_FILED = "id";

    public Answer createAnswer(AnswerRegisterRequestDto requestDto) {
        Question question = questionRepository.findById(requestDto.questionId()).orElseThrow(() -> new NotFoundDataException("Question not found"));

        Answer answer =answerRepository.save(Answer.builder()
                .question(question)
                .content(requestDto.content())
                .build());

        long id = answer.getId();
        System.out.println("id :" + id);
        return answer;
    }

    public Page<Answer> getAnswers(PageRequestDto pageRequestDto) {
        return PageableUtils.getPage(answerRepository, pageRequestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT_FILED);
    }
}
