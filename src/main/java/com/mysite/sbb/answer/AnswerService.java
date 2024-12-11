package com.mysite.sbb.answer;


import com.mysite.sbb.qustion.Question;
import com.mysite.sbb.qustion.QuestionRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AnswerService {
    private final AnswerRepository answerRepository;

    public List<Answer> getList(){
        return this.answerRepository.findAll();
    }

    public void create(Question question, String content) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        this.answerRepository.save(answer);
    }
}
