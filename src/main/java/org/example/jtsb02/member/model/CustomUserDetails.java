package org.example.jtsb02.member.model;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class CustomUserDetails extends User {

    private final Long id;
    private final String nickname;

    public CustomUserDetails(Long id, String nickname, String memberId, String password, Collection<? extends GrantedAuthority> authorities) {
        super(memberId, password, authorities);
        this.id = id;
        this.nickname = nickname;
    }
}
