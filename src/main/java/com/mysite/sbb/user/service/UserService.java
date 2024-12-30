package com.mysite.sbb.user.service;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.answer.repository.AnswerRepository;
import com.mysite.sbb.comment.entity.Comment;
import com.mysite.sbb.comment.repostitory.CommentRepository;
import com.mysite.sbb.global.exception.DataNotFoundException;
import com.mysite.sbb.question.entity.Question;
import com.mysite.sbb.question.repository.QuestionRepository;
import com.mysite.sbb.user.entity.SiteUser;
import com.mysite.sbb.user.entity.UserPostsDTO;
import com.mysite.sbb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;

    public SiteUser createUser(String username, String password, String email) {
        SiteUser newUser = new SiteUser();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setEmail(email);
        this.userRepository.save(newUser);
        return newUser;
    }

    public SiteUser findUser(String name) {
        Optional<SiteUser> siteUserOptional = this.userRepository.findByusername(name);
        if(siteUserOptional.isPresent()){
            return siteUserOptional.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public UserPostsDTO findAllPost(String username) {
        SiteUser user = findUser(username);
        List<Question> questionList = this.questionRepository.findAllByAuthorId(user.getId());
        List<Answer> answerList = this.answerRepository.findAllByAuthorId(user.getId());
        List<Comment> commentList = this.commentRepository.findAllByAuthorId(user.getId());
        return new UserPostsDTO(questionList, answerList, commentList);
    }
}
