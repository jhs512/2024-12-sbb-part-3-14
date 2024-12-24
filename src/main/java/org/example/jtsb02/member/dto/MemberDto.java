package org.example.jtsb02.member.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.jtsb02.member.entity.Member;

@Getter
@Builder
public class MemberDto {

    private Long id;
    private String memberId;
    private String nickname;
    private String email;

    public static MemberDto fromMember(Member member) {
        return MemberDto.builder()
            .id(member.getId())
            .memberId(member.getMemberId())
            .nickname(member.getNickname())
            .email(member.getEmail())
            .build();
    }
}
