package com.mysite.sbb.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.qustion.Question;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SiteUser {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String username;
    private String password;

    @Column(unique = true)
    private String email;

    @CreatedDate
    private LocalDateTime createDate;

    private UserRole userRole;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Question> questionList;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    private String providerTypeCode;

    public List<? extends GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        grantedAuthorities.add(new SimpleGrantedAuthority("member"));

        if ("admin".equals(username)) {
            grantedAuthorities.add(new SimpleGrantedAuthority("admin"));
        }

        return grantedAuthorities;
    }
}
