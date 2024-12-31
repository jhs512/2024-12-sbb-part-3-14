package com.mysite.sbb.category.entity;

import com.mysite.sbb.question.entity.Question;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CategorysListDTO {
    // 각 게시판별 "최근 5개의 게시물 리스트" 리스트
    private final List<List<Question>> categorysPosts;

    // 각 게시판별 한글 이름 (게시판 이름 표시는 이걸로 사용)
    private final List<String> categorysKorName;

    // 더보기 링크 이동용 카테고리 이름
    private final List<String> categorysName;
}
