package org.example.jtsb02.member.service;

import lombok.RequiredArgsConstructor;
import org.example.jtsb02.common.exception.DataNotFoundException;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.entity.Member;
import org.example.jtsb02.member.form.MemberForm;
import org.example.jtsb02.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void createMember(MemberForm memberForm) {
        memberRepository.save(Member.of(
            memberForm.getMemberId(),
            memberForm.getNickname(),
            passwordEncoder.encode(memberForm.getPassword()),
            memberForm.getEmail()
        ));
    }

    public MemberDto getMember(String memberId) {
        return MemberDto.fromMember(memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new DataNotFoundException("Member not found")));
    }
}