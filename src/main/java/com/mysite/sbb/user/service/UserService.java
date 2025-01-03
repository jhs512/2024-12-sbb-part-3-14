package com.mysite.sbb.user.service;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.answer.repository.AnswerRepository;
import com.mysite.sbb.comment.entity.Comment;
import com.mysite.sbb.comment.repostitory.CommentRepository;
import com.mysite.sbb.global.exception.DataNotFoundException;
import com.mysite.sbb.global.util.EmailService;
import com.mysite.sbb.question.entity.Question;
import com.mysite.sbb.question.repository.QuestionRepository;
import com.mysite.sbb.user.entity.SiteUser;
import com.mysite.sbb.user.entity.UserPostsDTO;
import com.mysite.sbb.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
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
    private final EmailService emailService;

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

    public void changeEmail(String username, String newEmail) {
        SiteUser user = findUser(username);
        user.setEmail(newEmail);
        this.userRepository.save(user);
    }

    public void changePassword(SiteUser user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        this.userRepository.save(user);
    }

    public void tempPassword(SiteUser user) throws MessagingException {
        // 임시 비밀번호 생성
        String tempPassword = generateTempPassword();

        // 임시 비밀번호로 교체
        changePassword(user, tempPassword);

        // 임시 비밀번호 발송
        System.out.println("임시 비밀번호 생성 : " + tempPassword);
        this.emailService.sendTempPassword(user.getEmail(), tempPassword);
    }

    // 임시 비밀번호 생성 메서드
    public String generateTempPassword() {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_-+=<>?";
        final int PASSWORD_LENGTH = 10; // 임시 비밀번호 길이

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }

    @Transactional
    public SiteUser socialLogin(String registrationId, String name, String email) {
        Optional<SiteUser> siteUserOptional = this.userRepository.findByusername(name + "(OAuth)");

        if(siteUserOptional.isPresent()){
            return siteUserOptional.get();
        }

        SiteUser user = new SiteUser();
        user.setRegistrationId(registrationId);
        user.setUsername(name + "(OAuth)");
        user.setEmail(email);
        user.setPassword("");
        this.userRepository.save(user);

        return user;
    }
}
