package org.example.jtsb02.category.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryForm {

    @NotEmpty(message = "카테고리명은 필수항목입니다.")
    @Size(max = 2000)
    private String name;
}
