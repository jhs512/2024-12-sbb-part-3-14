package com.mysite.sbb.user;


import com.mysite.sbb.Answer.Answer;
import com.mysite.sbb.Answer.AnswerRepository;
import com.mysite.sbb.Comment.Comment;
import com.mysite.sbb.Comment.CommentRepository;
import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.Question.Question;
import com.mysite.sbb.Question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    // 비밀번호 찾기: 임시 비밀번호 생성 및 이메일 발송
    public void resetPassword(String email) {
        SiteUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("Email not found"));

        String temporaryPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        userRepository.save(user);

        System.out.println("임시 비밀번호: " + temporaryPassword);
        System.out.println("DB에 저장된 비밀번호: " + user.getPassword());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("임시 비밀번호 안내");
        message.setText("임시 비밀번호: " + temporaryPassword + "\n로그인 후 비밀번호를 변경해주세요.");
        mailSender.send(message);
    }

    // 비밀번호 변경: 기존 비밀번호 확인 후 변경
    public void changePassword(String username, String currentPassword, String newPassword) {
        SiteUser user = userRepository.findByusername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword)); // 새 비밀번호 암호화
        userRepository.save(user);
    }

    // 작성한 질문 목록 가져오기
    public List<Question> getQuestionsByUser(String username) {
        return questionRepository.findByAuthor_Username(username);
    }

    // 작성한 답변 목록 가져오기
    public List<Answer> getAnswersByUser(String username) {
        return answerRepository.findByAuthor_Username(username);
    }

    // 작성한 댓글 목록 가져오기
    public List<Comment> getCommentsByUser(String username) {
        return commentRepository.findByAuthor_Username(username);
    }

}