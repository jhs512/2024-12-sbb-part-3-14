package com.mysite.sbb.catrgory;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.qustion.Question;
import com.mysite.sbb.qustion.QuestionService;
import com.mysite.sbb.user.SiteUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
@RequiredArgsConstructor
@Controller
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/insert")
    public String insert(CategoryForm categoryForm,  Principal principal){
        if(principal == null)
            return "redirect:/question/list/1";
        return "/category/create_category";
    }
    @PostMapping("/create")
    public String create(@Valid CategoryForm categoryForm,BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "redirect:/question/list/1";
        }
        categoryService.create(categoryForm.getContent());
        return "redirect:/question/list/1";
    }

}
