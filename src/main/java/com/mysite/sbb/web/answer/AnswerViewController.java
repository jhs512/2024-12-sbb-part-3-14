package com.mysite.sbb.web.answer;

import com.mysite.sbb.domain.answer.Answer;
import com.mysite.sbb.domain.answer.AnswerServiceImpl;
import com.mysite.sbb.web.answer.dto.request.AnswerRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static com.mysite.sbb.global.common.constant.PageConstants.ANSWER_FORM_VIEW;
import static com.mysite.sbb.global.util.CommonUtil.validateUserPermission;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerViewController {

    private final AnswerServiceImpl answerService;

    // 답변 수정 페이지 요청
    @PreAuthorize("isAuthenticated()")
    @GetMapping("modify/{id}")
    public String showAnswerModifyForm(AnswerRequestDTO answerRequestDTO, @PathVariable("id") Integer id, Principal principal) {
        Answer answer = answerService.getAnswer(id);
        validateUserPermission(principal.getName(), answer.getAuthor().getUsername(), "수정권한");
        answerRequestDTO.setContent(answer.getContent());
        return ANSWER_FORM_VIEW;
    }
}
