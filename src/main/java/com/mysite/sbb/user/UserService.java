package com.mysite.sbb.user;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public void modifyPassword(SiteUser siteUser, String password) {
        siteUser.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(siteUser);
    }

    public boolean isSamePassword(SiteUser siteUser, String password) {
        return passwordEncoder.matches(password, siteUser.getPassword());
    }

    public SiteUser getUserByEmail(String email) {
        Optional<SiteUser> os = this.userRepository.findByEmail(email);
        if (os.isPresent()) {
            return os.get();
        } else {
            throw new DataNotFoundException("siteuser not found.");
        }
    }

    public void delete(String email) {
        Optional<SiteUser> os = this.userRepository.findByEmail(email);
        os.ifPresent(this.userRepository::delete);
    }

    @Async
    public void sendPasswordResetMail(String email, String newPassword) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("비밀번호 찾기 메일"); // 제목 설정
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(email).append("의 비밀번호를 새롭게 발급하였습니다.")
                .append("새 비밀번호는 ").append(newPassword).append("입니다\n")
                .append("새 비밀번호를 통해 로그인 해주세요.");
        simpleMailMessage.setText(stringBuffer.toString()); // 내용 설정
        mailSender.send(simpleMailMessage);
    }
}
