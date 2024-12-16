package com.programmers.page;

import com.programmers.page.dto.PageRequestDto;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@UtilityClass
public class PageableUtils {
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_SIZE = 10;

    public static Pageable createPageable(PageRequestDto pageRequestDto) {
        int page = pageRequestDto.page() == null ? DEFAULT_PAGE : pageRequestDto.page();
        int size = pageRequestDto.size() == null ? DEFAULT_SIZE : pageRequestDto.size();
        return PageRequest.of(page, size);
    }
}
