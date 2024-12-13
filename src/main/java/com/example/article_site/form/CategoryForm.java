package com.example.article_site.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryForm {

    @NotEmpty(message = "카테코리 이름은 필수입니다.")
    private String name;
}
