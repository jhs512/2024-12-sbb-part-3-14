package org.example.jtsb02.member.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberForm {

    @NotEmpty(message = "아이디는 필수항목입니다.")
    @Size(min = 5, max = 20, message = "아이디는 5자 이상 20자 이하여야 합니다.")
    private String memberId;

    @NotEmpty(message = "닉네임은 필수항목입니다.")
    @Size(min = 2, max = 20)
    private String nickname;

    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    @Size(max = 20)
    private String password;

    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    @Size(max = 20)
    private String confirmPassword;

    @NotEmpty(message = "이메일 필수항목입니다.")
    @Email
    private String email;
}