package com.example.article_site.repository;

import com.example.article_site.domain.Author;
import com.example.article_site.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Question findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String content);
    Page<Question> findAll(Pageable pageable);
    Page<Question> findAll(Specification<Question> specification, Pageable pageable);
    Page<Question> findByCategory(String category, Pageable pageable);
    List<Question> findByAuthor(Author author);
}
