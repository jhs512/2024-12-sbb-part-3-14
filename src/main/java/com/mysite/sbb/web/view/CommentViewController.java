package com.mysite.sbb.web.view;

import com.mysite.sbb.domain.answer.service.AnswerServiceImpl;
import com.mysite.sbb.domain.comment.service.CommentServiceImpl;
import com.mysite.sbb.global.constant.View;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentViewController {

    private final CommentServiceImpl commentService;

    @GetMapping("/list")
    public String showCommentListForm(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "kw", defaultValue = "") String kw
    ) {
        model.addAttribute("paging", commentService.getList(page, kw));
        model.addAttribute("kw", kw);
        return View.Comment.LIST;
    }
}
