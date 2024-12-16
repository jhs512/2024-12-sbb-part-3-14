package com.example.article_site.repository;

import com.example.article_site.domain.Author;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
    Optional<Author> findByUsername(String username);
    Optional<Author> findByUsernameAndEmail(String username, String email);
    Optional<Author> findByEmail(String email);
}
