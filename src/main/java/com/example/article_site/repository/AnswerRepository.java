package com.example.article_site.repository;

import com.example.article_site.domain.Answer;
import com.example.article_site.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByAuthor(Author byUsername);
}
