package com.mysite.sbb.web.view;

import com.mysite.sbb.domain.answer.doamin.Answer;
import com.mysite.sbb.domain.answer.service.AnswerServiceImpl;
import com.mysite.sbb.global.constant.View;
import com.mysite.sbb.web.api.v1.answer.dto.request.AnswerRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

import static com.mysite.sbb.global.util.CommonUtil.validateUserPermission;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerViewController {

    private final AnswerServiceImpl answerService;

    @GetMapping("/list")
    public String showAnswerListForm(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "kw", defaultValue = "") String kw
    ) {
        model.addAttribute("paging", answerService.getList(page, kw));
        model.addAttribute("kw", kw);
        return View.Answer.LIST;
    }

    // 답변 수정 페이지 요청
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String showAnswerModifyForm(AnswerRequestDTO answerRequestDTO, @PathVariable("id") Integer id, Principal principal) {
        Answer answer = answerService.getAnswer(id);
        validateUserPermission(principal.getName(), answer.getAuthor().getUsername(), "수정권한");
        answerRequestDTO.setContent(answer.getContent());
        return View.Answer.FORM;
    }
}
