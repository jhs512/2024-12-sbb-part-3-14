package com.ll.pratice1.domain.answer.service;

import com.ll.pratice1.DataNotFoundException;
import com.ll.pratice1.domain.answer.Answer;
import com.ll.pratice1.domain.answer.repository.AnswerRepository;
import com.ll.pratice1.domain.question.Question;
import com.ll.pratice1.domain.question.repository.QuestionRepository;
import com.ll.pratice1.domain.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public Answer create(Question question, String content, SiteUser author){
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        this.answerRepository.save(answer);

        question.setRecentAnswerDate(LocalDateTime.now());
        this.questionRepository.save(question);

        return answer;
    }

    public Answer getAnswer(int id){
        Optional<Answer> answer = this.answerRepository.findById(id);
        if(answer.isPresent()){
            return answer.get();
        }else{
            throw new DataNotFoundException("answer not found");
        }
    }

    public List<Answer> getAnswerList(SiteUser siteUser){
        List<Answer> answerList = this.answerRepository.findByAuthor(siteUser);
        return answerList;
    }


    public Page<Answer> getAnswerList(Question question, int page, String sort) {
        List<Sort.Order> sorts = new ArrayList<>();
        Pageable pageable = null;
        if (sort.equals("latest")){
            sorts.add(Sort.Order.desc("createDate"));
            pageable = PageRequest.of(page, 5, Sort.by(sorts));
        }else if(sort.equals("vote")) {
            sorts.add(Sort.Order.desc("voter"));
            pageable = PageRequest.of(page, 5, Sort.by(sorts));
        }else{
            sorts.add(Sort.Order.desc("createDate"));
            pageable = PageRequest.of(page, 5, Sort.by(sorts));
        }
        // 페이징된 Answer 리스트 반환
        return answerRepository.findByQuestion(question, pageable);
    }

    public void modify(Answer answer, String content){
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    public void delete(Answer answer){
        this.answerRepository.delete(answer);
    }

    public void vote(Answer answer, SiteUser siteUser){
        answer.getVoter().add(siteUser);
        this.answerRepository.save(answer);
    }
}
