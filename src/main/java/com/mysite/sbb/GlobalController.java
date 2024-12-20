package com.mysite.sbb;

import com.mysite.sbb.catrgory.Category;
import com.mysite.sbb.catrgory.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalController {
    @Autowired
    CategoryService categoryService;
    @ModelAttribute
    public void handleRequest(HttpServletRequest request, Model model) {
        String requestURI = request.getRequestURI();
        List<Category> category = categoryService.getCategorys();
        model.addAttribute("categorys", category);
    }
}
