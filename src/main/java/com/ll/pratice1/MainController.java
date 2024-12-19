package com.ll.pratice1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String sbb(){
        return "redirect:/question/list";
    }
}
