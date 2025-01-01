package com.mysite.sbb.user.service;

import com.mysite.sbb.answer.repository.AnswerRepository;
import com.mysite.sbb.comment.repository.CommentRepository;
import com.mysite.sbb.entity.SiteUser;
import com.mysite.sbb.exception.DataNotFoundException;
import com.mysite.sbb.question.repository.QuestionRepository;
import com.mysite.sbb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;

    public SiteUser create(String username, String email,String password) {
        SiteUser siteUser = new SiteUser();
        siteUser.setUsername(username);
        siteUser.setEmail(email);
        siteUser.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(siteUser);
        return siteUser;
    }

    public String getCurrentUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof User) {
            return ((User) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);

        if(siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("User not found");
        }
    }

    public boolean isUsingTemporaryPassword(String username) {
        SiteUser user = userRepository.findByUsername(username)
                .orElse(null);

        return user.isTempPassword();
    }

    public SiteUser getUserByUsername(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);

        if(siteUser.isPresent()) {
            System.out.println("현재 사용자 이름: " + username);
            return siteUser.get();
        } else {
            System.out.println("사용자를 찾을 수 없습니다: " + username);
            throw new DataNotFoundException("User not found");
        }
    }

    public Map<String,Object> getUserProfile(String username, Pageable pageable) {
        SiteUser user = this.getUser(username);

        Map<String,Object> profileData = new HashMap<>();
        profileData.put("user", user);
        profileData.put("questions",questionRepository.findByAuthor(user,pageable));
        profileData.put("answers",answerRepository.findByAuthor(user,pageable));
        profileData.put("comments",commentRepository.findByAuthor(user,pageable));

        return profileData;
    }
}
