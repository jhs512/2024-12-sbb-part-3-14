package com.programmers.page.dto;

import jakarta.validation.constraints.Min;
import java.util.Map;

public record PageRequestDto(
        @Min(1)
        Integer page,

        @Min(1)
        Integer size,

        Map<String, Boolean> filters
) {
}
