package org.example.jtsb02.answer.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.answer.dto.AnswerDto;
import org.example.jtsb02.answer.entity.Answer;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.answer.repository.AnswerRepository;
import org.example.jtsb02.common.exception.DataNotFoundException;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.entity.Member;
import org.example.jtsb02.member.repository.MemberRepository;
import org.example.jtsb02.question.entity.Question;
import org.example.jtsb02.question.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;

    public Long createAnswer(Long questionId, AnswerForm answerForm, MemberDto memberDto) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new DataNotFoundException("Question not found"));
        return answerRepository.save(
            Answer.of(answerForm.getContent(), question, Member.fromMemberDto(memberDto))).getId();
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

    public void voteAnswer(Long answerId, MemberDto memberDto) {
        Answer answer = answerRepository.findById(answerId)
            .orElseThrow(() -> new DataNotFoundException("Answer not found"));
        Member member = memberRepository.findById(memberDto.getId())
            .orElseThrow(() -> new DataNotFoundException("Member not found"));
        if (answer.getVoter().contains(member)) {
            answer.getVoter().remove(member);
        } else {
            answer.getVoter().add(member);
        }
        answerRepository.save(answer);
    }

    public Page<AnswerDto> getAnswersByAuthorId(Long id, int page) {
        List<Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));
        return answerRepository.findByAuthorId(id, pageable).map(AnswerDto::fromAnswer);
    }

    public Page<AnswerDto> getAnswers(int page) {
        List<Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));
        return answerRepository.findAll(pageable).map(AnswerDto::fromAnswer);
    }
}
