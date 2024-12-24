package org.example.jtsb02.member.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.example.jtsb02.member.entity.Member;
import org.example.jtsb02.member.form.MemberForm;
import org.example.jtsb02.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원 가입")
    void createMember() {
        //given
        MemberForm memberForm = createMemberForm(
            "testId",
            "testNickname",
            "test1",
            "test1",
            "test1@gmail.com"
        );

        String encodedPassword = "encodedPassword";
        Member member = Member.of(
            memberForm.getMemberId(),
            memberForm.getNickname(),
            encodedPassword,
            memberForm.getEmail()
        );

        when(passwordEncoder.encode(memberForm.getPassword())).thenReturn(encodedPassword);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        //when
        memberService.createMember(memberForm);

        //then
        verify(passwordEncoder).encode(memberForm.getPassword());
        verify(memberRepository).save(any(Member.class));

    }

    private MemberForm createMemberForm(String memberId, String nickname, String password,
        String confirmPassword, String email) {
        MemberForm memberForm = new MemberForm();
        memberForm.setMemberId(memberId);
        memberForm.setNickname(nickname);
        memberForm.setPassword(password);
        memberForm.setConfirmPassword(confirmPassword);
        memberForm.setEmail(email);
        return memberForm;
    }
}