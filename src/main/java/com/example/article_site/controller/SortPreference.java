package com.example.article_site.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SortPreference {
    public static final String SORT_LATEST = "latest";
    private static final String SESSION_SORT_KEY = "answerSort";

    public String getCurrentSort(HttpSession session, String requestedSort) {
        if (requestedSort != null) {
            session.setAttribute(SESSION_SORT_KEY, requestedSort);
            return requestedSort;
        }

        String savedSort = (String) session.getAttribute(SESSION_SORT_KEY);
        if (savedSort == null) {
            savedSort = SORT_LATEST;
            session.setAttribute(SESSION_SORT_KEY, savedSort);
        }

        return savedSort;
    }
}
