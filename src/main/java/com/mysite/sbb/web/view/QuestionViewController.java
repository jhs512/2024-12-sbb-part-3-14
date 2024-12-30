package com.mysite.sbb.web.view;

import com.mysite.sbb.domain.comment.domain.Comment;
import com.mysite.sbb.domain.comment.service.CommentServiceImpl;
import com.mysite.sbb.domain.question.domain.Question;
import com.mysite.sbb.domain.question.service.QuestionServiceImpl;
import com.mysite.sbb.global.constant.View;
import com.mysite.sbb.web.api.v1.answer.dto.request.AnswerRequestDTO;
import com.mysite.sbb.web.api.v1.comment.dto.request.CommentRequestDTO;
import com.mysite.sbb.web.api.v1.question.dto.request.QuestionRequestDTO;
import com.mysite.sbb.web.api.v1.question.dto.response.QuestionDetailResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static com.mysite.sbb.global.util.CommonUtil.validateUserPermission;


@Controller
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionViewController {

    private final QuestionServiceImpl questionService;
    private final CommentServiceImpl commentServiceImpl;


    @GetMapping("list")
    public String showQuestionList(Model model,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "kw", defaultValue = "") String kw) {
        model.addAttribute("paging", questionService.getList(page, kw));
        model.addAttribute("kw", kw);
        return View.Question.LIST;
    }

    @GetMapping(value = "detail/{id}")
    public String showQuestionDetail(Model model,
                                     @PathVariable("id") Integer id,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "sortKeyword", defaultValue = "createDate") String sortKeyword) {

        // 질문 정보
        QuestionDetailResponseDTO question = this.questionService.getQuestionDetail(id, page, sortKeyword);

        // 질문에 대한 댓글
        List<Comment> commentOfQuestion = commentServiceImpl.getCommentsForQuestion(id);

        model.addAttribute("question", question);
        model.addAttribute("sort", sortKeyword); // 선택된 정렬 기준 전달
        model.addAttribute("questionComments", commentOfQuestion); // 질문 댓글
        model.addAttribute("answerComments", ""); // 답변 댓글
        model.addAttribute("answerRequestDTO", new AnswerRequestDTO()); // Form 초기화
        model.addAttribute("commentRequestDTO", CommentRequestDTO.empty());
        return View.Question.DETAIL;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("create")
    public String showQuestionForm(@ModelAttribute QuestionRequestDTO questionRequestDTO) {
        return View.Question.FORM;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("modify/{id}")
    public String showQuestionModifyForm(QuestionRequestDTO questionRequestDTO, @PathVariable("id") Integer id, Principal principal) {
        Question question = questionService.getQuestion(id);
        validateUserPermission(principal.getName(), question.getAuthor().getUsername(), "수정권한");
        questionRequestDTO.setSubject(question.getSubject());
        questionRequestDTO.setContent(question.getContent());
        return View.Question.FORM;
    }
}
