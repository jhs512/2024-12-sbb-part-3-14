package com.mysite.sbb.password.service;

import com.mysite.sbb.entity.SiteUser;
import com.mysite.sbb.password.form.ChangePasswordForm;
import com.mysite.sbb.user.repository.UserRepository;
import com.mysite.sbb.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public boolean sendTemporaryPassword(String email) {
        Optional<SiteUser> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            SiteUser user = userOptional.get();
            System.out.println("임시 비밀번호 생성 및 저장 완료: " + user.getUsername());

            // 임시 pw 생성
            String temporaryPassword = generateTempPassword();
            user.setPassword(encodePassword(temporaryPassword));
            user.setTempPassword(true);
            userRepository.save(user);

            emailService.sendTmpPassword(email,
                    "임시 비밀번호 발급",
                    "임시 비밀번호는 아래와 같습니다 : " + temporaryPassword);
            return true;
        }

        return false;
    }

    private String generateTempPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String encodePassword(String rawPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(rawPassword);
    }

    @Transactional
    public void updatePassword(String currentPassword, String newPassword) {
        String username = getCurrentUserName();

        SiteUser user = userService.getUserByUsername(username);

        // PW 비교
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            System.out.println("입력된 비밀번호가 현재 비밀번호와 일치하지 않음");
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(encodePassword(newPassword));
        user.setTempPassword(false);
        userRepository.save(user);
    }

    public String getCurrentUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof User) {
            return ((User) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
