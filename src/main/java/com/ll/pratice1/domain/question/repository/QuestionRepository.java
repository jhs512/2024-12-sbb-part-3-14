package com.ll.pratice1.domain.question.repository;

import com.ll.pratice1.domain.category.Category;
import com.ll.pratice1.domain.question.Question;
import com.ll.pratice1.domain.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer>{
    Page<Question> findAll(Pageable pageable);
    Page<Question> findAll(Specification<Question> spec, Pageable pageable);
    Page<Question> findAllByCategory(Category category, Specification<Question> spec, Pageable pageable);
    List<Question> findByAuthor(SiteUser siteUser);
}
