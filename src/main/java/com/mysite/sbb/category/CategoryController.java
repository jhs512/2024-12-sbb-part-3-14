package com.mysite.sbb.category;

import com.mysite.sbb.CommonUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/create")
    public String createCategory(
            CategoryForm categoryForm
    ){
        return "category_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String createCategory(
            @Valid CategoryForm categoryForm,
            BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "category_form";
        }
        Category category = categoryService.create(categoryForm.getTitle());
        return String.format("redirect:/question/list?category=%s", category.id);
    }
}
