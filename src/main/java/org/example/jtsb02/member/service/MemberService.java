package org.example.jtsb02.member.service;

import lombok.RequiredArgsConstructor;
import org.example.jtsb02.common.exception.DataNotFoundException;
import org.example.jtsb02.common.exception.PasswordNotMatchException;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.entity.Member;
import org.example.jtsb02.member.form.MemberForm;
import org.example.jtsb02.member.form.PasswordUpdateForm;
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

    public MemberDto getMemberByMemberId(String memberId) {
        return MemberDto.fromMember(memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new DataNotFoundException("Member not found")));
    }

    public MemberDto getMemberById(Long id) {
        return MemberDto.fromMember(memberRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Member not found")));
    }

    public void verifyPassword(Long id, PasswordUpdateForm passwordUpdateForm) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Member not found"));

        if (!passwordEncoder.matches(passwordUpdateForm.getOldPassword(), member.getPassword())) {
            throw new PasswordNotMatchException("기존 비밀번호가 일치하지 않습니다.");
        }
    }

    public void updatePassword(Long id, PasswordUpdateForm passwordUpdateForm) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Member not found"));
        memberRepository.save(member.toBuilder()
            .password(passwordEncoder.encode(passwordUpdateForm.getNewPassword()))
            .build());
    }

    public MemberDto updateTempPassword(String email, String tempPassword) {
        Member member = checkEmailExists(email);
        return MemberDto.fromMember(memberRepository.save(member.toBuilder()
            .password(passwordEncoder.encode(tempPassword))
            .build()));
    }

    private Member checkEmailExists(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new DataNotFoundException("email not found"));
    }
}