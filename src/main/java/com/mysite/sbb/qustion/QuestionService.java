package com.mysite.sbb.qustion;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.mysite.sbb.answer.Answer;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
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

    public void create(String subject, String content) {
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        this.questionRepository.save(question);
    }

    public Page<Question> getList(int page){
        Pageable pageable = PageRequest.of(page,10);
        return this.questionRepository.findAllByOrderByIdDesc(pageable);
    }
}
