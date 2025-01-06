package org.example.jtsb02.member.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateForm {

    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    @Size(max = 20)
    private String oldPassword;

    @NotEmpty(message = "새 비밀번호는 필수항목입니다.")
    @Size(max = 20)
    private String newPassword;

    @NotEmpty(message = "비밀번호 확인는 필수항목입니다.")
    @Size(max = 20)
    private String confirmPassword;
}
