package com.mysite.sbb.answer;
import com.mysite.sbb.qustion.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer>{
    void deleteById(Long placeId);
    Question findById(Long placeId);
    Page<Answer> findAllByQuestion(Question question, Pageable pageable);

}
