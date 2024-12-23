package org.example.jtsb02.answer.service;

import lombok.RequiredArgsConstructor;
import org.example.jtsb02.answer.entity.Answer;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.answer.repository.AnswerRepository;
import org.example.jtsb02.common.exception.DataNotFoundException;
import org.example.jtsb02.question.entity.Question;
import org.example.jtsb02.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public void createAnswer(Long questionId, AnswerForm answerForm) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new DataNotFoundException("Question not found"));
        answerRepository.save(Answer.of(answerForm.getContent(), question));
    }
}
