package com.mysite.sbb.qustion;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String content);
    Page<Question> findAll(Pageable pageable);
    Page<Question> findAllByOrderByIdDesc(Pageable pageable);
    void deleteById(Long placeId);
    Page<Question> findAll(Specification<Question> spec, Pageable pageable);
}
