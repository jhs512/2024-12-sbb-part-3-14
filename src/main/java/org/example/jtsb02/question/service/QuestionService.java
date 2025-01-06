package org.example.jtsb02.question.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.answer.dto.AnswerDto;
import org.example.jtsb02.answer.repository.AnswerRepository;
import org.example.jtsb02.category.entity.Category;
import org.example.jtsb02.category.repository.CategoryRepository;
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
    private final AnswerRepository answerRepository;
    private final CategoryRepository categoryRepository;

    public Long createQuestion(QuestionForm questionForm, MemberDto memberDto) {
        Category category = categoryRepository.findById(questionForm.getCategoryId())
            .orElseThrow(() -> new DataNotFoundException("Category not found"));
        return questionRepository.save(
            Question.of(questionForm.getSubject(), questionForm.getContent(),
                Member.fromMemberDto(memberDto), category)).getId();
    }

    public Page<QuestionDto> getQuestions(int page, String kw) {
        List<Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));
        return questionRepository.findAllByKeyword(kw, pageable).map(QuestionDto::fromQuestion);
    }

    public QuestionDto getQuestion(Long id) {
        return QuestionDto.fromQuestion(questionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Question not found")));
    }

    public QuestionDto getQuestionWithHitsCount(Long id, int page, String sort) {
        Pageable pageable = getPageable(page, sort);
        Page<AnswerDto> answerPage = answerRepository.findByQuestionId(id, pageable)
            .map(AnswerDto::fromAnswer);
        return QuestionDto.fromQuestion(addHits(questionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Question not found"))), answerPage);
    }

    private Pageable getPageable(int page, String sort) {
        List<Sort.Order> sorts = new ArrayList<>();
        if (sort.isEmpty() || sort.equals("old")) {
            sorts.add(Sort.Order.asc("createdAt"));
        } else if(sort.equals("new")) {
            sorts.add(Sort.Order.desc("createdAt"));
        } else if(sort.equals("recommend")) {
            sorts.add(Sort.Order.desc("voter"));
        }
        return PageRequest.of(page - 1, 10, Sort.by(sorts));
    }

    private Question addHits(Question question) {
        return questionRepository.save(question.toBuilder().hits(question.getHits() + 1).build());
    }

    public void modifyQuestion(Long id, QuestionForm questionForm) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Question not found"));
        Category category = categoryRepository.findById(questionForm.getCategoryId())
            .orElseThrow(() -> new DataNotFoundException("Category not found"));
        questionRepository.save(question.toBuilder()
            .category(category)
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
        if (question.getVoter().contains(Member.fromMemberDto(memberDto))) {
            question.getVoter().remove(Member.fromMemberDto(memberDto));
        } else {
            question.getVoter().add(Member.fromMemberDto(memberDto));
        }
        questionRepository.save(question);
    }

    public Page<QuestionDto> getQuestionsByAuthorId(Long authorId, int page) {
        List<Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));
        return questionRepository.findByAuthorId(authorId, pageable).map(QuestionDto::fromQuestion);
    }
}
