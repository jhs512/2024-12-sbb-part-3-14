package com.programmers.answer;

import com.programmers.answer.dto.AnswerRegisterRequestDto;
import com.programmers.exception.NotFoundDataException;
import com.programmers.question.Question;
import com.programmers.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public Answer createAnswer(AnswerRegisterRequestDto requestDto) {
        Question question = questionRepository.findById(requestDto.questionId()).orElseThrow(() -> new NotFoundDataException("Question not found"));

        return answerRepository.save(Answer.builder()
                        .question(question)
                        .content(requestDto.content())
                .build());
    }
}
