package com.programmers.page;

import com.programmers.page.dto.PageRequestDto;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PageableUtils {
    public static Pageable createPageable(PageRequestDto pageRequestDto, int defaultSize, String defaultSort) {
        int page = Objects.requireNonNullElse(pageRequestDto.page(), 1);
        int size = Objects.requireNonNullElse(pageRequestDto.size(), defaultSize);
        boolean desc = Objects.requireNonNullElse(pageRequestDto.desc(), true);

        String sort = pageRequestDto.sort();
        if(sort.isBlank()){
            sort = defaultSort;
        }
        return PageRequest.of(page - 1, size, Sort.by(desc ? Sort.Direction.DESC : Sort.Direction.ASC, sort ));
    }
}
