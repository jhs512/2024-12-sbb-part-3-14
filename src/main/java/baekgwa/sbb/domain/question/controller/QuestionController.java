package baekgwa.sbb.domain.question.controller;

import baekgwa.sbb.domain.answer.form.AnswerForm;
import baekgwa.sbb.domain.answer.form.CommentForm;
import baekgwa.sbb.domain.question.dto.QuestionDto;
import baekgwa.sbb.domain.question.dto.QuestionDto.CategoryInfo;
import baekgwa.sbb.domain.question.form.QuestionForm;
import baekgwa.sbb.domain.question.service.QuestionService;
import baekgwa.sbb.global.util.ControllerUtils;
import baekgwa.sbb.model.category.entity.CategoryType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final ControllerUtils controllerUtils;

    @GetMapping("/detail/{id}")
    public String detail(
            Model model,
            Principal principal,
            @PathVariable("id") Integer id,
            @ModelAttribute(name = "answerForm") AnswerForm answerForm,
            @ModelAttribute(name = "commentForm") CommentForm commentForm,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        controllerUtils.restoreBindingResultFlashAttributesToModel(request, model, "answerForm");
        String username = principal == null ? null : principal.getName();
        QuestionDto.DetailInfo question = questionService.getQuestion(id, username, page, size);
        model.addAttribute("question", question);
        return "question_detail";
    }

    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }

    @PostMapping("/create")
    public String questionCreate(
            @Valid QuestionForm questionForm,
            BindingResult bindingResult,
            Principal principal
    ) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        questionService.create(questionForm.getSubject(), questionForm.getContent(),
                principal.getName());
        return "redirect:/question/list";
    }

    @GetMapping("/list")
    public String list(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "categoryType", defaultValue = "QUESTION") String categoryType
    ) {
        Page<QuestionDto.MainInfo> paging = questionService.getList(page, size, keyword,
                categoryType);
        List<QuestionDto.CategoryInfo> category = questionService.getCategory();
        model.addAttribute("paging", paging);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        return "question_list";
    }

    @GetMapping("/modify/{id}")
    public String questionModify(
            @ModelAttribute(name = "questionForm") QuestionForm questionForm,
            @PathVariable("id") Integer id,
            Principal principal
    ) {
        QuestionDto.ModifyInfo question = questionService.getQuestion(id);
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
            Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        questionService.modifyQuestion(id, principal.getName(), questionForm);
        return String.format("redirect:/question/detail/%s", id);
    }

    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        questionService.deleteQuestion(id, principal.getName());
        return "redirect:/";
    }

    @GetMapping("/vote/{id}")
    public String questionVote(
            Principal principal,
            @PathVariable("id") Integer id) {
        questionService.vote(id, principal.getName());
        return String.format("redirect:/question/detail/%s", id);
    }

    @GetMapping("/vote/cancel/{id}")
    public String questionVoteCancel(
            Principal principal,
            @PathVariable("id") Integer id) {
        questionService.voteCancel(id, principal.getName());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PostMapping("/comment/{questionId}")
    public String createQuestionComment(
            @PathVariable("questionId") Integer questionId,
            @RequestParam("content") String content,
            Principal principal
    ) {
        questionService.createComment(content, principal.getName(), questionId);
        return String.format("redirect:/question/detail/%s", questionId);
    }
}
