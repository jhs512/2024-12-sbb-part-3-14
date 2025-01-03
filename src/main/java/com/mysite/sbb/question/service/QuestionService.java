package com.mysite.sbb.question.service;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.category.entity.Category;
import com.mysite.sbb.category.entity.CategorysListDTO;
import com.mysite.sbb.category.service.CategoryService;
import com.mysite.sbb.comment.repostitory.CommentRepository;
import com.mysite.sbb.global.exception.DataNotFoundException;
import com.mysite.sbb.question.entity.Question;
import com.mysite.sbb.question.repository.QuestionRepository;
import com.mysite.sbb.user.entity.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final CommentRepository commentRepository;
    private final CategoryService categoryService;

    private Specification<Question> search(String kw, Category category) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> question, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);

                Join<Question, SiteUser> questionAuthor = question.join("author", JoinType.LEFT);
                Join<Question, Answer> answer = question.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> answerAuthor = answer.join("author", JoinType.LEFT);

                return cb.and(cb.equal(question.get("category"), category),
                        cb.or(cb.like(question.get("subject"), "%" + kw + "%"),
                            cb.like(question.get("content"), "%" + kw + "%"),
                            cb.like(questionAuthor.get("username"), "%" + kw + "%"),
                            cb.like(answer.get("content"), "%" + kw + "%"),
                            cb.like(answerAuthor.get("username"), "%" + kw + "%")));
            }
        };
    }

    public Page<Question> findAll(int page, String kw, Category category, int pageSize) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));
        Specification<Question> specification = search(kw, category);
        return this.questionRepository.findAll(specification, pageable);
    }

    public Page<Question> findAll(int page, String kw, Category category) {
        return findAll(page, kw, category, 10);
    }

    public List<Question> findAll(Category category) {
        return findAll(0, "", category, 5).getContent();
    }

    public Question findQuestionById(Integer id) {
        Optional<Question> questionOptional = this.questionRepository.findById(id);
        if(questionOptional.isPresent()) {
            return questionOptional.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public Question createQuestion(String subject, String content, SiteUser author, Category category) {
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setCreateDate(LocalDateTime.now());
        question.setAuthor(author);
        question.setCategory(category);
        question.setViewCounter(0);
        this.questionRepository.save(question);
        return question;
    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(Question question) {
        this.questionRepository.delete(question);
        this.commentRepository.deleteByQuestionId(question.getId());
    }

    public void voteQuestion(Question question, SiteUser siteUser) {
        if(question.getVoter().contains(siteUser)){
            question.getVoter().remove(siteUser);
        } else {
            question.getVoter().add(siteUser);
        }
        this.questionRepository.save(question);
    }

    public CategorysListDTO findAllRecentPosts() {
        List<List<Question>> categorysPosts = new ArrayList<>();
        List<String> categorysKorName = new ArrayList<>();
        List<String> categorysName = new ArrayList<>();

        List<Category> categoryList = this.categoryService.getList();
        for(Category category : categoryList){
            //카테고리별 이름들 넣기
            categorysKorName.add(category.getCategoryKorName());
            categorysName.add(category.getCategoryName());

            //각 카테고리별 최신 게시물 5개 가져오기
            List<Question> recentPosts = this.findAll(category);

            //가져온 게시물 리스트를 categorysPosts 넣기
            categorysPosts.add(recentPosts);
        }
        return new CategorysListDTO(categorysPosts, categorysKorName, categorysName);
    }

    public void addViewCount(Question question) {
        question.setViewCounter(question.getViewCounter()+1);
        this.questionRepository.save(question);
    }
}
