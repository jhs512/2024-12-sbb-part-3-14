package com.mysite.sbb.answer.service;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.answer.repository.AnswerRepository;
import com.mysite.sbb.global.exception.DataNotFoundException;
import com.mysite.sbb.question.entity.Question;
import com.mysite.sbb.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;

    public Answer createAnswer(Question question, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);

        this.answerRepository.save(answer);

        return answer;
    }

    public Answer findAnswer(Integer id) {
        Optional<Answer> answerOptional = this.answerRepository.findById(id);
        if(answerOptional.isPresent()) {
            return answerOptional.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modifyAnswer(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }

    public void voteAnswer(Answer answer, SiteUser voteUser) {
        if(answer.getVoter().contains(voteUser)){
            answer.getVoter().remove(voteUser);
        } else {
            answer.getVoter().add(voteUser);
        }
        this.answerRepository.save(answer);
    }

    public Page<Answer> getAnswers(Question question, int page) {
        Pageable pageable = PageRequest.of(page, 3);
        return this.answerRepository.findByQuestionId(question.getId(), pageable);
    }

    public Page<Answer> getAnswersByVotes(Question question, int page) {
        Pageable pageable = PageRequest.of(page, 3);
        return this.answerRepository.findByQuestionIdOrderByVoterDesc(question.getId(), pageable);
    }

    public Page<Answer> getAnswersByCreateDate(Question question, int page) {
        Pageable pageable = PageRequest.of(page, 3);
        return this.answerRepository.findByQuestionIdOrderByCreateDateDesc(question.getId(), pageable);
    }
}
