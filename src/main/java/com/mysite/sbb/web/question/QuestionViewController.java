package com.mysite.sbb.web.question;

import com.mysite.sbb.domain.comment.Comment;
import com.mysite.sbb.domain.comment.CommentService;
import com.mysite.sbb.domain.question.Question;
import com.mysite.sbb.domain.question.QuestionServiceImpl;
import com.mysite.sbb.web.answer.dto.request.AnswerRequestDTO;
import com.mysite.sbb.web.comment.dto.request.CommentRequestDTO;
import com.mysite.sbb.web.question.dto.request.QuestionRequestDTO;
import com.mysite.sbb.web.question.dto.response.QuestionDetailResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static com.mysite.sbb.global.common.constant.PageConstants.*;
import static com.mysite.sbb.global.common.validator.SecurityValidaotr.validateUserPermission;

@Controller
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionViewController {

    private final QuestionServiceImpl questionService;
    private final CommentService commentService;


    @GetMapping("list")
    public String showQuestionList(Model model,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "kw", defaultValue = "") String kw) {
        model.addAttribute("paging", questionService.getList(page, kw));
        model.addAttribute("kw", kw);
        return QUESTION_LIST_VIEW;
    }

    @GetMapping(value = "detail/{id}")
    public String showQuestionDetail(Model model,
                                     @PathVariable("id") Integer id,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "sortKeyword", defaultValue = "createDate") String sortKeyword) {

        // 질문 정보
        QuestionDetailResponseDTO question = this.questionService.getQuestionDetail(id, page, sortKeyword);

        // 댓글 정보
        List<Comment> comments = commentService.getCommentsForQuestion(Long.parseLong(String.valueOf(id)));

        model.addAttribute("question", question);
        model.addAttribute("sort", sortKeyword); // 선택된 정렬 기준 전달
        model.addAttribute("comments", comments);
        model.addAttribute("answerRequestDTO", new AnswerRequestDTO()); // Form 초기화
        model.addAttribute("commentRequestDTO", CommentRequestDTO.empty());
        return QUESTION_DETAIL_VIEW;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("create")
    public String showQuestionForm(@ModelAttribute QuestionRequestDTO questionRequestDTO) {
        return QUESTION_FORM_VIEW;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("modify/{id}")
    public String showQuestionModifyForm(QuestionRequestDTO questionRequestDTO, @PathVariable("id") Integer id, Principal principal) {
        Question question = questionService.getQuestion(id);
        validateUserPermission(principal.getName(), question.getAuthor().getUsername(), "수정권한");
        questionRequestDTO.setSubject(question.getSubject());
        questionRequestDTO.setContent(question.getContent());
        return QUESTION_FORM_VIEW;
    }
}
