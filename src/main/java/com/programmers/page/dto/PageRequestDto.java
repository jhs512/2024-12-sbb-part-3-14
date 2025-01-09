package com.programmers.page.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;

public record PageRequestDto(
        @Min(1)
        Integer page,

        @Min(1)
        Integer size,

        @Nullable
        String sort,

        @Nullable
        Boolean desc
) {
}
