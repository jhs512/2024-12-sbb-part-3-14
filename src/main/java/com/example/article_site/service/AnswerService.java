package com.example.article_site.service;

import com.example.article_site.domain.Answer;
import com.example.article_site.domain.Author;
import com.example.article_site.domain.Comment;
import com.example.article_site.domain.Question;
import com.example.article_site.dto.AnswerDetailDto;
import com.example.article_site.dto.AnswerListDto;
import com.example.article_site.dto.profile.AnswerProfileDto;
import com.example.article_site.dto.profile.QuestionProfileDto;
import com.example.article_site.exception.DataNotFoundException;
import com.example.article_site.repository.AnswerRepository;
import com.example.article_site.repository.CommentRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.article_site.domain.Comment.createComment;
import static com.example.article_site.dto.AnswerDetailDto.createAnswerDetailDto;

@Service
@Transactional
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final AuthorService authorService;
    private final CommentRepository commentRepository;
    private final static int ANSWER_PAGE_SIZE = 10;

    public Answer create(Question question, String content, Author author) {
        return answerRepository.save(Answer.createAnswer(question, content, author));
    }

    public void modify(Answer answer, String content) {
        Answer.modifyAnswer(answer, content);
        answerRepository.save(answer);
    }

    public Answer getAnswer(Long id) {
        Optional<Answer> answer = answerRepository.findById(id);
        if(answer.isEmpty()){
            throw new DataNotFoundException("Answer Not Found");
        }
        return answer.get();
    }

    public void delete(Answer answer){
        answerRepository.delete(answer);
    }

    public void vote(Answer answer, Author author) {
        answer.getVoter().add(author);
        answerRepository.save(answer);
    }

    public AnswerDetailDto getAnswerDetailDto(Long id) {
        Optional<Answer> byId = answerRepository.findById(id);
        if(byId.isEmpty()){
            throw new DataNotFoundException("Answer Not Found");
        }
        return createAnswerDetailDto(byId.get());
    }

    public void addComment(Long id, String content, String name) {
        Answer answer = getAnswer(id);
        Author author = authorService.findByUsername(name);
        Comment comment =  createComment(answer, author, content);
        commentRepository.save(comment);
    }

    public Page<AnswerListDto> getAnswerDtoPage(int page) {
        Pageable pageable = PageRequest.of(page, ANSWER_PAGE_SIZE);
        return answerRepository.findAll(pageable)
                .map(AnswerListDto::createAnswerListDto);
    }

    public List<AnswerProfileDto> getAnswerProfileDtoList(String name) {
        return answerRepository.findByAuthor(authorService.findByUsername(name)).stream()
                .map(AnswerProfileDto::createAnswerProfileDto)
                .toList();
    }
}
