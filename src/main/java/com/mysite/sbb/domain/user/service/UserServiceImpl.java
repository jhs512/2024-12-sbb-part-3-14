package com.mysite.sbb.domain.user;


import com.mysite.sbb.domain.answer.AnswerRepository;
import com.mysite.sbb.domain.comment.Comment;
import com.mysite.sbb.domain.comment.CommentRepository;
import com.mysite.sbb.domain.question.QuestionRepository;
import com.mysite.sbb.global.exception.DataNotFoundException;
import com.mysite.sbb.global.util.CommonUtil;
import com.mysite.sbb.global.util.PasswordUtil;
import com.mysite.sbb.web.api.v1.answer.dto.response.AnswerResponseDTO;
import com.mysite.sbb.web.api.v1.question.dto.response.QuestionListResponseDTO;
import com.mysite.sbb.web.api.v1.user.dto.request.ChangeRequestDTO;
import com.mysite.sbb.web.api.v1.user.dto.request.ForgotRequestDTO;
import com.mysite.sbb.web.api.v1.user.dto.response.UserResponseDTO;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository; // 사용자 정보를 저장 및 조회하는 레포지토리
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 PasswordEncoder
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;
    private final CommonUtil commonUtil;

    @Override
    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username); // 사용자 이름 설정
        user.setEmail(email); // 이메일 설정
        user.setPassword(passwordEncoder.encode(password)); // 비밀번호 암호화 후 설정
        userRepository.save(user); // 데이터베이스에 사용자 저장
        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public SiteUser getUser(String username) {
        return userRepository.findByusername(username)
                .orElseThrow(() -> new DataNotFoundException("not found user :" + username));
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDTO getProfile(String username) {
        SiteUser user = userRepository.findByusername(username)
                .orElseThrow(() -> new DataNotFoundException("not found user: " + username));

        List<QuestionListResponseDTO> questions = questionRepository.findTop5ByAuthorUsernameOrderByCreateDateDesc(username)
                .stream()
                .map(QuestionListResponseDTO::new)
                .toList();

        List<AnswerResponseDTO> answers = answerRepository.findTop5ByAuthorUsernameOrderByCreateDateDesc(username)
                .stream()
                .map(AnswerResponseDTO::new)
                .toList();

        List<Comment> comments = commentRepository.findTop5ByAuthorUsernameOrderByCreateDateDesc(username);

        return new UserResponseDTO(
                user.getUsername(),
                user.getEmail(),
                questions,
                answers,
                comments
        );

    }

    @Transactional
    public boolean sendPasswordResetEmail(ForgotRequestDTO dto) {

        boolean UserExist = userRepository.existsByUsernameAndEmail(dto.username(), dto.email());

        if (UserExist) {
            // 임시 비밀번호 생성
            String temporaryPassword = PasswordUtil.generateTemporaryPassword();

            // 사용자 비밀번호 업데이트
            SiteUser user = userRepository.findByusername(dto.username())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다 :" + dto.username()));
            user.setPassword(passwordEncoder.encode(temporaryPassword));
            userRepository.save(user);

            // 이메일 발송
            try {
                commonUtil.sendPassowrdResetEmail(dto.email(), temporaryPassword);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }

            return true;
        }

        return false;
    }

    public boolean changePassword(ChangeRequestDTO dto, String username) {
        try {
            // 사용자 검증 및 현재 비밀번호 확인
            SiteUser user = userRepository.findByusername(username)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

            // 현재 비밀번호 확인
            if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
            }

            // 새 비밀번호와 새 비밀번호 확인 비교
            if (!dto.newPassword().equals(dto.confirmNewPassword())) {
                throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
            }

            // 새 비밀번호 저장
            user.setPassword(passwordEncoder.encode(dto.newPassword()));
            userRepository.save(user);
            return true;

        } catch (Exception e) {
            log.error("비밀번호 중 예기치 않은 오류 발생 - 사용자 : {}", username, e);
            return false;
        }
    }

}
