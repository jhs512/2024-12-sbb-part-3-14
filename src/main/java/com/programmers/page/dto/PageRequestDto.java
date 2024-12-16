package com.programmers.page.dto;

import jakarta.validation.constraints.Min;

public record PageRequestDto(
        @Min(1)
        Integer page,

        @Min(1)
        Integer size,

        String sort,

        Boolean desc
) {
}
