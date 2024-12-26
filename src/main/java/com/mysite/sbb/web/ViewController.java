package com.mysite.sbb.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static com.mysite.sbb.web.common.constant.PageConstants.QUESTION_LIST_VIEW;

@Controller
public class ViewController {

    @GetMapping("/")
    public String root() {
        return "redirect:/question/list";
    }

}
