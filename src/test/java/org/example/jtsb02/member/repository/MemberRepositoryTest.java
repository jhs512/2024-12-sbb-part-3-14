package org.example.jtsb02.member.repository;

import static org.assertj.core.api.Assertions.*;

import org.example.jtsb02.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 가입")
    void df() {
        //given
        Member member = Member.of("testId", "testNickname", "test1", "test1@gmail.com");

        //when
        Member result = memberRepository.save(member);

        //then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(result.getNickname()).isEqualTo(member.getNickname());
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
        assertThat(result.getRole()).isEqualTo(member.getRole());
    }
}