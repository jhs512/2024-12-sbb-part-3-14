package com.example.article_site.dto.profile;

import com.example.article_site.domain.Author;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AuthorProfileDto {
    private String name;
    private String email;

    public static AuthorProfileDto createAuthorProfileDto(Author author) {
        AuthorProfileDto dto = new AuthorProfileDto();
        dto.setName(author.getUsername());
        dto.setEmail(author.getEmail());
        return dto;
    }
}
