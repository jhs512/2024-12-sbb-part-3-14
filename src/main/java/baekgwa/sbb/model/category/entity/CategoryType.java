package baekgwa.sbb.model.category.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CategoryType {
    QUESTION("질문"),
    COURSE("강좌"),
    FREEBOARD("자유게시판")
    ;

    private final String categoryName;
}
