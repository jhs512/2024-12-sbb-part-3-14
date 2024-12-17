package com.programmers.page;

import com.programmers.exception.PageOutOfRangeException;
import com.programmers.page.dto.PageRequestDto;
import java.util.Map;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

@UtilityClass
public class PageableUtils {
    public static Pageable createPageable(PageRequestDto pageRequestDto, int defaultSize, String defaultSort) {
        int page = Objects.requireNonNullElse(pageRequestDto.page(), 1);
        int size = Objects.requireNonNullElse(pageRequestDto.size(), defaultSize);
        Map<String, Boolean> filters = pageRequestDto.filters();

        Sort sort;

        if (filters == null) {
            sort = Sort.by(Sort.Direction.DESC, defaultSort);
        } else {
            sort = Sort.by(filters.entrySet()
                    .stream()
                    .map(entry -> {
                        boolean desc = entry.getValue() != null && entry.getValue();
                        return desc
                                ? Sort.Order.desc(entry.getKey())
                                : Sort.Order.asc(entry.getKey());
                    })
                    .toList());
        }
        return PageRequest.of(page - 1, size, sort);
    }


    public static <T> Page<T> getPage(JpaRepository<T, Long> jpaRepository, PageRequestDto requestDto, int defaultPageSize, String defaultSortField){
        Pageable pageable = createPageable(requestDto, defaultPageSize, defaultSortField);
        long totalCount = jpaRepository.count();
        long maxPage = totalCount / pageable.getPageSize() + totalCount % pageable.getPageSize() == 0 ? 0 : 1;

        if (maxPage < pageable.getPageNumber() + 1) {
            throw new PageOutOfRangeException("page out of range");
        }else{
            return jpaRepository.findAll(pageable);
        }
    }
}
