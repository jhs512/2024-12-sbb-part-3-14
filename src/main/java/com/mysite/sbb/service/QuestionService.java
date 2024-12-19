package com.mysite.sbb.service;

import com.mysite.sbb.domain.SiteUser;
import com.mysite.sbb.domain.Question;
import com.mysite.sbb.dto.QuestionDetailDTO;
import com.mysite.sbb.dto.QuestionListDTO;
import com.mysite.sbb.form.QuestionForm;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface QuestionService {

    // 질문 목록 조회
    List<QuestionListDTO> getAllQuestions();

    // 페이징과 검색 조건을 포함한 질문 목록 조회
    Page<QuestionListDTO> getList(int page, String kw);

    // 질문 엔티티 조회
    Question getQuestion(Integer id);

    // 질문 상세 정보를 위한 DTO 조회
    QuestionDetailDTO getQuestionDetail(Integer id);

    // 질문 생성
    void create(QuestionForm questionForm, String userName);

    // 질문 수정
    void modify(Integer id, QuestionForm questionForm, String username);

    // 질문 삭제
    void delete(Integer id, String userName);

    // 질문 추천
    void vote(Integer id, String userName);

    // 검색
    Specification<Question> search(String kw);
}
