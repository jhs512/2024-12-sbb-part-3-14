package com.site.sss.Category;

import lombok.Getter;

@Getter
public enum CategoryNum {

    QUESTION(1),
    LEARNING(2),
    FREE(3);

    private final int value;

    CategoryNum(int value) {  // 생성자
        this.value = value;
    }


}
