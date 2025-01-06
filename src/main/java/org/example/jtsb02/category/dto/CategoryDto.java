package org.example.jtsb02.category.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.jtsb02.category.entity.Category;

@Getter
@Builder
public class CategoryDto {

    private Long id;
    private String name;

    public static CategoryDto fromCategory(Category category) {
        return CategoryDto.builder()
            .id(category.getId())
            .name(category.getName())
            .build();
    }
}
