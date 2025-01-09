package com.mysite.sbb.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserForm {

    @NotEmpty(message="아이디는 필수항목입니다.")
    @Size(min = 3, max = 25)
    private String userName;

    @NotEmpty(message="내용은 필수항목입니다.")
    @Size(min = 8, max = 30)
    private String password;

    @NotEmpty(message="내용은 필수항목입니다.")
    @Size(min = 8, max = 30)
    private String pwCheck;

    @NotEmpty(message="내용은 필수항목입니다.")
    private String email;
}
