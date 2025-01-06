package org.example.jtsb02.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.category.form.CategoryForm;
import org.example.jtsb02.category.service.CategoryService;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String createCategory(CategoryForm categoryForm, Model model) {
        model.addAttribute("categoryForm", categoryForm);
        return "category/form/create";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createCategory(@Valid CategoryForm categoryForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "category/form/create";
        }
        categoryService.createCategory(categoryForm);
        return "redirect:/";
    }
}
