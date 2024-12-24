package org.example.jtsb02.answer.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.answer.dto.AnswerDto;
import org.example.jtsb02.answer.entity.Answer;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.answer.repository.AnswerRepository;
import org.example.jtsb02.common.exception.DataNotFoundException;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.entity.Member;
import org.example.jtsb02.question.entity.Question;
import org.example.jtsb02.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public void createAnswer(Long questionId, AnswerForm answerForm, MemberDto memberDto) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new DataNotFoundException("Question not found"));
        answerRepository.save(
            Answer.of(answerForm.getContent(), question, Member.fromMemberDto(memberDto)));
    }

    public AnswerDto getAnswer(Long answerId) {
        return AnswerDto.fromAnswer(answerRepository.findById(answerId)
            .orElseThrow(() -> new DataNotFoundException("Answer not found")));
    }

    public void modifyAnswer(Long answerId, AnswerForm answerForm) {
        Answer answer = answerRepository.findById(answerId)
            .orElseThrow(() -> new DataNotFoundException("Answer not found"));
        answerRepository.save(answer.toBuilder()
            .content(answerForm.getContent())
            .modifiedAt(LocalDateTime.now())
            .build());
    }

    public void deleteAnswer(AnswerDto answerDto) {
        answerRepository.delete(Answer.fromAnswerDto(answerDto));
    }
}
