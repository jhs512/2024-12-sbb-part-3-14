package com.mysite.sbb.controller.util;

import com.mysite.sbb.web.question.dto.response.QuestionListResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionTestFixture {

    public static class Builder {
        private int totalCount = 0;
        private int pageSize = 10;
        private int currentPage = 0;
        private String searchKeyword = "";

        public Builder withTotalCount(int count) {
            this.totalCount = count;
            return this;
        }

        public Builder withPageSize(int size) {
            this.pageSize = size;
            return this;
        }

        public Builder withCurrentPage(int page) {
            this.currentPage = page;
            return this;
        }

        public Builder withSearchKeyword(String keyword) {
            this.searchKeyword = keyword;
            return this;
        }

        public Page<QuestionListResponseDTO> build() {
            List<QuestionListResponseDTO> allQuestions = createQuestions(totalCount);

            // 검색어가 있는 경우 필터링
            if (!searchKeyword.isEmpty()) {
                allQuestions = allQuestions.stream()
                        .filter(q -> q.getAuthorName().contains(searchKeyword))
                        .collect(Collectors.toList());
            }

            // 페이징 처리
            PageRequest pageRequest = PageRequest.of(
                    currentPage,
                    pageSize,
                    Sort.by(Sort.Direction.DESC, "createDate")
            );

            int start = currentPage * pageSize;
            int end = Math.min(start + pageSize, allQuestions.size());

            // 페이지에 해당하는 데이터만 추출
            List<QuestionListResponseDTO> pageContent =
                    start < allQuestions.size() ?
                            allQuestions.subList(start, end) :
                            new ArrayList<>();

            return new PageImpl<>(
                    pageContent,
                    pageRequest,
                    allQuestions.size()
            );
        }

        private List<QuestionListResponseDTO> createQuestions(int count) {
            List<QuestionListResponseDTO> questions = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                questions.add(createQuestion(i));
            }
            return questions;
        }

        private QuestionListResponseDTO createQuestion(int index) {
            QuestionListResponseDTO dto = new QuestionListResponseDTO();
            dto.setId(index + 1L);
            dto.setSubject("테스트 질문 " + (index + 1));
            dto.setContent("테스트 내용 " + (index + 1));
            dto.setCreateDate(LocalDateTime.now().minusDays(index));
            dto.setAuthorName("테스트 작성자" + index % 3);
            dto.setAnswerCount(index % 3);
            return dto;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
