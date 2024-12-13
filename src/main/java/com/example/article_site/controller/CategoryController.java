package com.example.article_site.controller;

import com.example.article_site.form.CategoryForm;
import com.example.article_site.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(CategoryForm form) {
        return "category_form";
    }

    @PreAuthorize(" isAuthenticated()")
    @PostMapping("/create")
    public String create(@Valid CategoryForm form,
                         BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "category_form";
        }
        categoryService.create(form.getName());
        return "redirect:/question/list";
    }
}
