package com.mysite.sbb.domain.answer;


import com.mysite.sbb.domain.question.Question;
import com.mysite.sbb.domain.user.SiteUser;
import com.mysite.sbb.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;

    @Override
    public Answer create(Question question, String content, SiteUser author) {
        return createAnswer(question, content, author);
    }

    private Answer createAnswer(Question question, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setQuestion(question);
        answer.setAuthor(author);
        return answerRepository.save(answer);
    }

    @Override
    @Transactional(readOnly = true)
    public Answer getAnswer(Integer id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Answer not found with id: " + id));
    }

    @Override
    public void modify(Answer answer, String content) {
        answer.setContent(content);
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
