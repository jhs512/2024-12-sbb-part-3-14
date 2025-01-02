package com.mysite.sbb.domain.question.service;

import com.mysite.sbb.domain.question.domain.Question;
import com.mysite.sbb.web.api.v1.question.dto.request.QuestionRequestDTO;
import com.mysite.sbb.web.api.v1.question.dto.response.QuestionDetailResponseDTO;
import com.mysite.sbb.web.api.v1.question.dto.response.QuestionListResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

public interface QuestionService {

    // 페이징과 검색 조건을 포함한 질문 목록 조회
    Page<QuestionListResponseDTO> getList(int page, String kw);

    // 질문 엔티티 조회
    Question getQuestion(Integer id);

    // 질문 상세 정보를 위한 DTO 조회
    QuestionDetailResponseDTO getQuestionDetail(Integer id, int page, String sortKeyword);

    // 질문 생성
    void create(QuestionRequestDTO questionRequestDTO, String userName);

    // 질문 수정
    void modify(Integer id, QuestionRequestDTO questionRequestDTO, String username);

    // 질문 삭제
    void delete(Integer id, String userName);

    // 질문 추천
    void vote(Integer id, String userName);

    // 검색
    Specification<Question> search(String kw);
}
