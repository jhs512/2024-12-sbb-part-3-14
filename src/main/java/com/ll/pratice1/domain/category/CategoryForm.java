package com.ll.pratice1.domain.category;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryForm {
    @NotEmpty(message="내용은 필수항목입니다.")
    @Size(max=50)
    private String category;
}
