package com.mysite.sbb.controller.util;

import com.mysite.sbb.web.question.dto.response.QuestionListResponseDTO;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

public class TestUtil {
    @SneakyThrows
    public static void printHTTP(MvcResult result) {
        System.out.println("\n=== HTTP 응답 정보 ===");
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Headers: " + result.getResponse().getHeaderNames());

        String redirectedUrl = result.getResponse().getRedirectedUrl();
        if (redirectedUrl != null) {
            System.out.println("Redirected URL: " + redirectedUrl);
        }
    }

    public static void printModelAndView(MvcResult result) {
        System.out.println("\n=== 모델 정보 ===");
        ModelAndView mav = result.getModelAndView();
        if (mav != null) {
            System.out.println("View: " + mav.getViewName());
            System.out.println("Model: " + mav.getModel());
        }
    }

    public static void printPaging(MvcResult result) {
        ModelAndView mav = result.getModelAndView();
        if (mav == null || !mav.getModel().containsKey("paging")) {
            return;
        }

        Page<QuestionListResponseDTO> resultPage =
                (Page<QuestionListResponseDTO>) mav.getModel().get("paging");

        System.out.println("\n=== 페이징 결과 ===");
        System.out.println("총 항목 수: " + resultPage.getTotalElements());
        System.out.println("총 페이지 수: " + resultPage.getTotalPages());
        System.out.println("현재 페이지 크기: " + resultPage.getSize());
        System.out.println("현재 페이지 내용: ");
        resultPage.getContent().forEach(q ->
                System.out.println("제목: " + q.getSubject() + " 작가: " + q.getAuthorName())
        );
    }

    public static void printTestResults(MvcResult result) {
        printHTTP(result);
        printModelAndView(result);
        printPaging(result);
    }
}
