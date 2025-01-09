package com.mysite.sbb.answer.service;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.answer.repository.AnswerRepository;
import com.mysite.sbb.question.entity.Question;
import com.mysite.sbb.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Answer write(Question question, String content, SiteUser author) {
        Answer answer = Answer.builder()
                .content(content)
                .question(question)
                .author(author)
                .build();
        return answerRepository.save(answer);
    }

    public Answer findById(long id) {
        Optional<Answer> op = answerRepository.findById(id);
        if (op.isPresent()) {
            return op.get();
        } else {
            throw new RuntimeException("answer not found");
        }
    }

    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }

    public void vote(Answer answer, SiteUser siteUser) {
        answer.getVoter().add(siteUser);
        answer.setVoterCount(answer.getVoter().size());
        answerRepository.save(answer);
    }

    public Page<Answer> findByQuestionId(long id, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("voterCount"));
        sorts.add(Sort.Order.desc("modifiedDate"));
        Pageable pageable = PageRequest.of(page, 3, Sort.by(sorts));

        return answerRepository.findByQuestionId(id, pageable);
    }
}
