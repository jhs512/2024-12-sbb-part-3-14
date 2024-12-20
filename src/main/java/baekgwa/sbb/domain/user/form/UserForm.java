package baekgwa.sbb.domain.user.form;

import baekgwa.sbb.global.annotation.validation.user.Email;
import baekgwa.sbb.global.annotation.validation.user.Password;
import baekgwa.sbb.global.annotation.validation.user.Username;
import lombok.Getter;
import lombok.Setter;

public class UserForm {

    @Getter
    @Setter
    public static class Signup {
        @Username private String username;
        @Password private String password1;
        @Password private String password2;
        @Email private String email;
    }
}
