package com.mysite.sbb.answer;
import java.util.List;

import com.mysite.sbb.qustion.Question;
import com.mysite.sbb.qustion.QuestionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Controller
public class AnswerController {
    private final AnswerRepository answerRepository;
    @GetMapping("/answer/list")
    public String list(Model model){
        model.addAttribute("b","a");
        return "answer_list";
    }

    @GetMapping("/")
    public String root(Model model){
        return "redirect:/answer/list";
    }
}
