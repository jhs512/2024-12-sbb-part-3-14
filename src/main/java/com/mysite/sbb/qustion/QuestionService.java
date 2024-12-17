package com.mysite.sbb.qustion;
import java.time.LocalDateTime;
import java.util.List;

import com.mysite.sbb.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.mysite.sbb.answer.Answer;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;

import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> getList(){
        return this.questionRepository.findAll();
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(SiteUser siteUser, String subject, String content) {
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setUser(siteUser);
        this.questionRepository.save(question);
    }

    public Page<Question> getList(int page){
        Pageable pageable = PageRequest.of(page,10);
        return this.questionRepository.findAllByOrderByIdDesc(pageable);
    }
    public void delete(int id){
        this.questionRepository.deleteById(id);
    }
    public void vote(Question question,SiteUser siteUser){
        question.getVoterSet().add(siteUser);
        this.questionRepository.save(question);
    }
    //1.userlist가져와서 자바에서 탐색
    //2.user객체 가져온다음 db에서 호출
    public Question modify(int id, String subject, String content){
            Question question = getQuestion(id);
            question.setSubject(content);
            question.setContent(subject);
            return question;
    }
}
