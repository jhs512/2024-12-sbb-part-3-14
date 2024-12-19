package com.mysite.sbb.answer;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Answer create(Question question, String content, SiteUser author) {
        Answer answer = Answer
                .builder()
                .content(content)
                .question(question)
                .author(author)
                .build();

        this.answerRepository.save(answer);
        return answer;
    }

    public Answer getAnswer(Integer id) {
        Optional<Answer> answer = answerRepository.findById(id);
        if(answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modify(Answer answer, String content) {
        answer.setContent(content);
        this.answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }

    public void vote(Answer answer, SiteUser siteUser){
        answer.getVoter().add(siteUser);
        this.answerRepository.save(answer);
    }

    public Page<Answer> getList(int questionId, int page) {
        int itemsPerPage = 5;
        Pageable pageable = PageRequest.of(page, itemsPerPage);
        return this.answerRepository.findAnswerByQuestionIdOrderByVoterCountDesc(questionId, pageable);
    }

    public Page<Answer> getMyAnswerList(String username, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));

        int itemsPerPage = 5;
        Pageable pageable = PageRequest.of(page, itemsPerPage, Sort.by(sorts));
        return this.answerRepository.findAllByAuthor_Username(username, pageable);
    }

    public int getPageNumber(int answerId) {
        Optional<Answer> optionalAnswer = this.answerRepository.findById(answerId);
        if(optionalAnswer.isEmpty()) throw new DataNotFoundException("존재하지 않는 답변입니다.");

        Answer answer = optionalAnswer.get();
        List<Answer> answerList = this.answerRepository.findAllByQuestion_Id(answer.getQuestion().getId());

        answerList.sort((o1, o2) -> Integer.compare(o2.getVoter().size(), o1.getVoter().size()));
        int index = answerList.indexOf(answer);

        int itemsPerPage = 5;
        return index / itemsPerPage;
    }
}
