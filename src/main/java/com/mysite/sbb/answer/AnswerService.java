package com.mysite.sbb.answer;


import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.qustion.Question;
import com.mysite.sbb.qustion.QuestionRepository;
import com.mysite.sbb.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class AnswerService {
    private final AnswerRepository answerRepository;

    public List<Answer> getList(){
        return this.answerRepository.findAll();
    }

    public void create(SiteUser siteUser, Question question, String content) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setUser(siteUser);

        this.answerRepository.save(answer);
    }
    public void delete(int id){
        this.answerRepository.deleteById(id);
    }
    public Question getQestion(int id){
        return this.answerRepository.findById(id).get().getQuestion();
    }
    public Answer getAnswer(int id){
        return this.answerRepository.findById(id).get();

    }
    public Page<Answer> getList(Question question , int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 3, Sort.by(sorts));
        return this.answerRepository.findAllByQuestion(question,pageable);
    }
    public Answer modify(int id,String content){
        Optional<Answer> optional_answer = this.answerRepository.findById(id);
        if (optional_answer.isPresent()) {
            Answer answer= optional_answer.get();
            answer.setContent(content);
            return answer;
        } else {
            throw new DataNotFoundException("question not found");
        }
    }
    public void vote(Answer answer,SiteUser siteUser){
        answer.getVoterSet().add(siteUser);
        this.answerRepository.save(answer);
    }
}
