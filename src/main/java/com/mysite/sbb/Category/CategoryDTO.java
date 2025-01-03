package com.mysite.sbb.Category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Integer id;       // 카테고리 ID
    private String name;      // 카테고리 이름
    private String description; // 카테고리 설명
}