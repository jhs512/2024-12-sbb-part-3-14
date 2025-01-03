package com.mysite.sbb.Question;

import com.mysite.sbb.Answer.Answer;
import com.mysite.sbb.Answer.AnswerService;
import com.mysite.sbb.Category.Category;
import com.mysite.sbb.Category.CategoryService;
import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.Utils.MarkdownService;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerService answerService;
    private final CategoryService categoryService;
    private final MarkdownService markdownService;


    // 전체 질문 리스트 (필터 없이)
    public List<Question> getList() {
        return this.questionRepository.findAll();
    }

    // 질문 생성
    public void create(String subject, String content, Integer categoryId, SiteUser user) {
        Category category = categoryService.findById(categoryId); // 바로 Category 사용
        if (category == null) {
            throw new DataNotFoundException("Category not found");
        }
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setCreateDate(LocalDateTime.now());
        question.setAuthor(user);
        question.setCategory(category);
        this.questionRepository.save(question);
    }

    // 질문 페이징 (카테고리 필터 추가)
    public Page<Question> getListByCategory(int page, String kw, Integer categoryId) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createDate")));
        Specification<Question> spec = search(kw);
        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }
        return this.questionRepository.findAll(spec, pageable);
    }

    // 질문 상세 조회
    public Question getQuestion(Integer id) {
        return this.questionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("question not found"));
    }

    // 질문 수정
    public void modify(Question question, String subject, String content, Integer categoryId) {
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            throw new DataNotFoundException("Category not found");
        }
        question.setSubject(subject);
        question.setContent(content);
        question.setCategory(category);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    // 질문 삭제
    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    // 질문 추천
    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    // 검색 및 필터링
    private Specification<Question> search(String kw) {
        return (Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            query.distinct(true);
            Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
            Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
            Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
            return cb.or(
                    cb.like(q.get("subject"), "%" + kw + "%"),
                    cb.like(q.get("content"), "%" + kw + "%"),
                    cb.like(u1.get("username"), "%" + kw + "%"),
                    cb.like(a.get("content"), "%" + kw + "%"),
                    cb.like(u2.get("username"), "%" + kw + "%")
            );
        };
    }

    // 검색 및 페이징 처리
    public Map<String, Object> searchAll(String kw, int page, Integer categoryId) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createDate")));
        Specification<Question> spec = search(kw);
        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }
        Page<Question> questions = this.questionRepository.findAll(spec, pageable);
        Page<Answer> answers = answerService.searchAnswers(kw, page);

        Map<String, Object> results = new HashMap<>();
        results.put("questions", questions);
        results.put("answers", answers);
        return results;
    }

    // 조회 수 증가
    public void incrementViewCount(Integer id) {
        Question question = getQuestion(id); // 질문 가져오기
        question.incrementViewCount();       // 조회 수 증가
        questionRepository.save(question);   // 데이터베이스에 저장
    }


    // 질문 상세 조회 (HTML 변환)
    public Question getRenderedQuestion(Integer id) {
        Question question = getQuestion(id); // 기존 상세 조회
        question.setContent(markdownService.renderMarkdownToHtml(question.getContent())); // HTML 변환
        return question;
    }



}