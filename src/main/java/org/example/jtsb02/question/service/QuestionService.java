package org.example.jtsb02.question.service;

import lombok.RequiredArgsConstructor;
import org.example.jtsb02.question.entity.Question;
import org.example.jtsb02.question.form.QuestionForm;
import org.example.jtsb02.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public void createQuestion(QuestionForm questionForm) {
        questionRepository.save(Question.of(questionForm.getSubject(), questionForm.getContent()));
    }
}
