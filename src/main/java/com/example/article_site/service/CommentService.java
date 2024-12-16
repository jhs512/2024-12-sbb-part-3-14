package com.example.article_site.service;

import com.example.article_site.dto.profile.AnswerProfileDto;
import com.example.article_site.dto.profile.CommentProfileDto;
import com.example.article_site.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final AuthorService authorService;

    public List<CommentProfileDto> getCommentProfileDtoList(String name) {
        return commentRepository.findByAuthor(authorService.findByUsername(name)).stream()
                .map(CommentProfileDto::createCommentProfileDto)
                .toList();
    }
}
