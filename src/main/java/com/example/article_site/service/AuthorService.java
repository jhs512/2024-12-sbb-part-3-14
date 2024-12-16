package com.example.article_site.service;

import com.example.article_site.domain.Author;
import com.example.article_site.dto.profile.AuthorProfileDto;
import com.example.article_site.exception.DataNotFoundException;
import com.example.article_site.form.ModifyPasswordForm;
import com.example.article_site.form.SignupForm;
import com.example.article_site.repository.AuthorRepository;
import com.example.article_site.security.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.article_site.domain.Author.createAuthor;
import static com.example.article_site.dto.profile.AuthorProfileDto.createAuthorProfileDto;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthorService implements UserDetailsService {

    private final AuthorRepository authorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Author> _siteUser = authorRepository.findByUsername(username);
        if (_siteUser.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Author siteUser = _siteUser.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("admin".equals(username)) authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        else authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        return new User(siteUser.getUsername(), siteUser.getPassword(), authorities);
    }

    public Author create(SignupForm signUpForm){
        Author author = createAuthor(
                signUpForm.getUsername(),
                signUpForm.getEmail(),
                passwordEncoder.encode(signUpForm.getPassword1())
        );
        authorRepository.save(author);
        return author;
    }

    public Author findByUsername(String username){
        Optional<Author> byUsername = authorRepository.findByUsername(username);
        if(byUsername.isPresent()){
            return byUsername.get();
        }else{
            throw new DataNotFoundException("Author not found");
        }
    }

    public boolean modifyPassword(ModifyPasswordForm passwordModifyForm, String name) {
        Author author = findByUsername(name);
        if(!passwordEncoder.matches(passwordModifyForm.getOldPassword(), author.getPassword())
        || !passwordModifyForm.getNewPassword().equals(passwordModifyForm.getCheckPassword())){
            log.info("Before Encode {}", passwordModifyForm.getOldPassword());
            log.info("Old password {} Author.getPassword {}", passwordEncoder.encode(passwordModifyForm.getOldPassword()), author.getPassword());
            return false;
        }
        author.modifyPassword(passwordEncoder.encode(passwordModifyForm.getNewPassword()));
        authorRepository.save(author);
        return true;
    }

    public Optional<Author> checkUserPresent(String username, String email) {
        return authorRepository.findByUsernameAndEmail(username, email);
    }

    public String createNewPassword(Author author) {
        String newPassword = UUID.randomUUID().toString().substring(0, 10);
        author.modifyPassword(passwordEncoder.encode(newPassword));
        authorRepository.save(author);
        return newPassword;
    }

    public Optional<Author> findByEmail(String email) {
        return authorRepository.findByEmail(email);
    }

    public Author save(Author author) {
        return authorRepository.save(author);
    }

    public AuthorProfileDto getAuthorProfileDto(String name) {
        return createAuthorProfileDto(findByUsername(name));
    }
}
