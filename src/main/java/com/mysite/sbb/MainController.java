package com.mysite.sbb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    @GetMapping("/")
    public String home() {
        return "redirect:/question/list";
    }

    @GetMapping("/about")
    @ResponseBody
    public String about() {
        System.out.println("about");
        return "hello";
    }
}
