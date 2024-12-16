package baekgwa.sbb.domain.user.form;

import baekgwa.sbb.global.annotation.user.Email;
import baekgwa.sbb.global.annotation.user.Password;
import baekgwa.sbb.global.annotation.user.Username;
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
