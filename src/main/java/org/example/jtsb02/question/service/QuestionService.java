package org.example.jtsb02.question.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.common.exception.DataNotFoundException;
import org.example.jtsb02.question.dto.QuestionDto;
import org.example.jtsb02.question.entity.Question;
import org.example.jtsb02.question.form.QuestionForm;
import org.example.jtsb02.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Long createQuestion(QuestionForm questionForm) {
        return questionRepository.save(
            Question.of(questionForm.getSubject(), questionForm.getContent())).getId();
    }

    public List<QuestionDto> getQuestions() {
        return questionRepository.findAll().stream().map(QuestionDto::fromQuestion).toList();
    }

    public QuestionDto getQuestion(Long id) {
        return QuestionDto.fromQuestion(addHits(questionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Question not found"))));
    }

    private Question addHits(Question question) {
        return questionRepository.save(question.toBuilder().hits(question.getHits() + 1).build());
    }

    public void modifyQuestion(Long id, QuestionForm questionForm) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Question not found"));
        questionRepository.save(question.toBuilder()
            .subject(questionForm.getSubject())
            .content(questionForm.getContent())
            .modifiedAt(LocalDateTime.now())
            .build());
    }

    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Question not found"));
        questionRepository.delete(question);
    }
}
