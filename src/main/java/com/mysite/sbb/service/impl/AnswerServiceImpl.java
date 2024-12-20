package com.mysite.sbb.service.impl;

import com.mysite.sbb.model.answer.entity.Answer;
import com.mysite.sbb.model.question.entity.Question;
import com.mysite.sbb.model.user.entity.SiteUser;
import com.mysite.sbb.global.exception.DataNotFoundException;
import com.mysite.sbb.repository.AnswerRepository;
import com.mysite.sbb.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;


    @Override
    public void create(Question question, String content) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        this.answerRepository.save(answer);
    }

    @Override
    public Answer create(Question question, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        this.answerRepository.save(answer);
        return answer;
    }

    @Override
    public Answer getAnswer(Integer id) {
        Optional<Answer> answer = this.answerRepository.findById(id);

        if(answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    @Override
    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    @Override
    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }

    @Override
    public void vote(Answer answer, SiteUser siteUser) {
        answer.getVoter().add(siteUser);
        this.answerRepository.save(answer);
    }
}
