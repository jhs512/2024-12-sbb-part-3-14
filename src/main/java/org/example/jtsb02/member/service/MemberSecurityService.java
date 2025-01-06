package org.example.jtsb02.member.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.member.entity.Member;
import org.example.jtsb02.member.model.CustomUserDetails;
import org.example.jtsb02.member.model.MemberRole;
import org.example.jtsb02.member.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberSecurityService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (member.getRole() != null && member.getRole() == MemberRole.ADMIN) {
            authorities.add(new SimpleGrantedAuthority(MemberRole.ADMIN.getRole()));
        } else {
            authorities.add(new SimpleGrantedAuthority(MemberRole.USER.getRole()));
        }

        return new CustomUserDetails(member.getId(), member.getNickname(), member.getMemberId(), member.getPassword(), authorities);
    }
}
