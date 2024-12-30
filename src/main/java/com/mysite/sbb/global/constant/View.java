package com.mysite.sbb.global.constant;

public final class View {

    private View() {
        // 인스턴스화 방지
    }

    /* 질문 관련 뷰 */
    public static final class Question {
        public static final String LIST = "question/list";
        public static final String DETAIL = "question/detail";
        public static final String FORM = "question/form";
    }

    /* 답변 관련 뷰 */
    public static final class Answer {
        public static final String LIST = "answer/list";
        public static final String FORM = "answer/form";
    }

    /* 댓글 관련 뷰 */
    public static final class Comment {
        public static final String LIST = "comment/list";
    }

    /* 사용자 관련 뷰 */
    public static final class User {
        public static final String LOGIN = "user/login";
        public static final String SIGNUP = "user/signup";
        public static final String PROFILE = "user/profile";
        public static final String FORGOT_PASSWORD = "user/forgot-password";
    }

    /* 이메일 템플릿 */
    public static final class Email {
        public static final String PASSWORD_RESET = "email/password-reset";
    }
}