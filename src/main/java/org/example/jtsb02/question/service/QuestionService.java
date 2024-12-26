package org.example.jtsb02.question.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.common.exception.DataNotFoundException;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.entity.Member;
import org.example.jtsb02.question.dto.QuestionDto;
import org.example.jtsb02.question.entity.Question;
import org.example.jtsb02.question.form.QuestionForm;
import org.example.jtsb02.question.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Long createQuestion(QuestionForm questionForm, MemberDto memberDto) {
        return questionRepository.save(
            Question.of(questionForm.getSubject(), questionForm.getContent(),
                Member.fromMemberDto(memberDto))).getId();
    }

    public Page<QuestionDto> getQuestions(int page) {
        List<Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));
        return questionRepository.findAll(pageable).map(QuestionDto::fromQuestion);
    }

    public QuestionDto getQuestion(Long id) {
        return QuestionDto.fromQuestion(questionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Question not found")));
    }

    public QuestionDto getQuestionWithHitsCount(Long id) {
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

    public void voteQuestion(Long id, MemberDto memberDto) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Question not found"));
        question.getVoter().add(Member.fromMemberDto(memberDto));
        questionRepository.save(question);
    }
}
