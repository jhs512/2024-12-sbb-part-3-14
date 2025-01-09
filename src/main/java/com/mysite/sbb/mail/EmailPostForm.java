package com.mysite.sbb.mail;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailPostForm {
    private String email;
}