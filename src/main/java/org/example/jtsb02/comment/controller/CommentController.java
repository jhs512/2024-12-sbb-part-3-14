package org.example.jtsb02.comment.controller;

import lombok.RequiredArgsConstructor;
import org.example.jtsb02.comment.dto.CommentDto;
import org.example.jtsb02.comment.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/list")
    public String getComments(@RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "kw", defaultValue = "") String kw, Model model) {
        Page<CommentDto> comments = commentService.getComments(page);
        model.addAttribute("paging", comments);
        model.addAttribute("kw", kw);
        return "comment/list";
    }
}
