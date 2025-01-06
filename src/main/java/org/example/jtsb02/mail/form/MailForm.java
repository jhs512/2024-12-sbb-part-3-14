package org.example.jtsb02.mail.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailForm {

    @Email
    @NotEmpty(message = "이메일은 필수항목입니다.")
    private String email;
}
