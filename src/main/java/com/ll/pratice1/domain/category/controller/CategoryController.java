package com.ll.pratice1.domain.category.controller;

import com.ll.pratice1.domain.category.Category;
import com.ll.pratice1.domain.category.CategoryForm;
import com.ll.pratice1.domain.category.service.CategoryService;
import com.ll.pratice1.domain.user.SiteUser;
import com.ll.pratice1.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(CategoryForm categoryForm){
        return "category_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(Principal principal,
                         @Valid CategoryForm categoryForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "category_form";
        }
        SiteUser siteUser = userService.getUser(principal.getName());
        this.categoryService.create(siteUser, categoryForm.getCategory());
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete")
    public String questionDelete(Principal principal, @RequestParam("category")String category){
        Category ctg = this.categoryService.getCategory(category);
        if (!ctg.getSiteUser().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.categoryService.delete(category);
        return "redirect:/";
    }
}
