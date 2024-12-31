package com.mysite.sbb.domain.user.service;


import com.mysite.sbb.domain.answer.repository.AnswerRepository;
import com.mysite.sbb.domain.comment.domain.Comment;
import com.mysite.sbb.domain.comment.repository.CommentRepository;
import com.mysite.sbb.domain.question.repository.QuestionRepository;
import com.mysite.sbb.domain.user.domain.SiteUser;
import com.mysite.sbb.domain.user.repository.UserRepository;
import com.mysite.sbb.global.email.EmailService;
import com.mysite.sbb.global.exception.DataNotFoundException;
import com.mysite.sbb.global.util.PasswordUtil;
import com.mysite.sbb.web.api.v1.answer.dto.response.AnswerResponseDTO;
import com.mysite.sbb.web.api.v1.question.dto.response.QuestionListResponseDTO;
import com.mysite.sbb.web.api.v1.user.dto.request.ChangeRequestDTO;
import com.mysite.sbb.web.api.v1.user.dto.request.ForgotRequestDTO;
import com.mysite.sbb.web.api.v1.user.dto.response.UserResponseDTO;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;
    private final EmailService emailService;

    @Override
    public SiteUser create(String username, String email, String password) {
        validateNewUser(username, email);
        return createAndSaveUser(username, email, password);
    }

    private void validateNewUser(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new DataIntegrityViolationException("이미 존재하는 사용자명입니다.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException("이미 등록된 이메일입니다.");
        }
    }

    private SiteUser createAndSaveUser(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public SiteUser getUser(String username) {
        return findUserByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDTO getProfile(String username) {
        SiteUser user = findUserByUsername(username);
        return UserResponseDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .questions(getLatest5Questions(username))
                .answers(getLatest5Answers(username))
                .comments(getLatest5Comments(username))
                .build();
    }

    private List<Comment> getLatest5Comments(String username) {
        return commentRepository.findTop5ByAuthorUsernameOrderByCreateDateDesc(username);
    }

    private List<AnswerResponseDTO> getLatest5Answers(String username) {
        return answerRepository.findTop5ByAuthorUsernameOrderByCreateDateDesc(username)
                .stream()
                .map(AnswerResponseDTO::new)
                .toList();
    }

    private List<QuestionListResponseDTO> getLatest5Questions(String username) {
        return questionRepository.findTop5ByAuthorUsernameOrderByCreateDateDesc(username)
                .stream()
                .map(QuestionListResponseDTO::new)
                .toList();
    }

    @Transactional
    public boolean sendPasswordResetEmail(ForgotRequestDTO dto) {

        try {
            SiteUser user = findUserByUsername(dto.username());
            String temporaryPassword = resetUserPassword(user);
            emailService.sendPasswordResetEmail(user.getEmail(), temporaryPassword);
            return true;
        } catch (MessagingException e) {
            log.error("비밀번호 초기화 실패 - 사용자: {}", dto.username(), e);
            return false;
        }
    }

    public boolean changePassword(ChangeRequestDTO dto, String username) {
        try {
            SiteUser user = findUserByUsername(username);
            validatePasswords(dto, user);
            newPasswordAndSave(user, dto.newPassword());
            return true;
        } catch (Exception e) {
            log.error("비밀번호 중 예기치 않은 오류 발생 - 사용자 : {}", username, e);
            return false;
        }
    }

    private String resetUserPassword(SiteUser user) {
        String temporaryPassword = PasswordUtil.generateTemporaryPassword();
        newPasswordAndSave(user, temporaryPassword);
        return temporaryPassword;
    }

    private void newPasswordAndSave(SiteUser user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private void validatePasswords(ChangeRequestDTO dto, SiteUser user) {
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        if (!dto.newPassword().equals(dto.confirmNewPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }
    }

    private SiteUser findUserByUsername(String username) {
        return userRepository.findByusername(username)
                .orElseThrow(() -> new DataNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }


    public void socialLogin(String code, String registrationId) {
        System.out.println("code = " + code);
        System.out.println("registrationId = " + registrationId);
    }
}
