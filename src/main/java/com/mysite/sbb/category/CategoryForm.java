package com.mysite.sbb.category;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryForm {
    @NotEmpty(message = "카테고리 이름은 필수입니다.")
    @Size(max = 20, message = "가능한 최대 길이는 20자입니다.")
    private String title;
}
