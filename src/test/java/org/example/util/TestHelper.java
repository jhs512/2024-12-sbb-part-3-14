package org.example.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import org.example.jtsb02.answer.entity.Answer;
import org.example.jtsb02.answer.form.AnswerForm;
import org.example.jtsb02.category.entity.Category;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.entity.Member;
import org.example.jtsb02.member.form.MemberForm;
import org.example.jtsb02.question.entity.Question;
import org.example.jtsb02.question.form.QuestionForm;

public class TestHelper {

    public static Category createCategory() {
        return Category.builder()
            .id(1L)
            .name("질문답변")
            .build();
    }

    public static QuestionForm createQuestionForm(Long categoryId, String subject, String content) {
        QuestionForm questionForm = new QuestionForm();
        questionForm.setCategoryId(categoryId);
        questionForm.setSubject(subject);
        questionForm.setContent(content);
        return questionForm;
    }

    public static Question createQuestion(Long id, QuestionForm questionForm, Category category) {
        return Question.builder()
            .id(id)
            .category(category)
            .subject(questionForm.getSubject())
            .content(questionForm.getContent())
            .createdAt(LocalDateTime.now())
            .hits(0)
            .answers(new ArrayList<>())
            .author(Member.builder()
                .id(1L)
                .memberId("onlyTest")
                .nickname("onlyTest")
                .password("onlyTest")
                .email("onlyTest@gmail.com")
                .build())
            .voter(new HashSet<>())
            .comments(new ArrayList<>())
            .build();
    }

    public static String generateStringOfLength(int length) {
        return "a".repeat(length);
    }

    public static MemberForm createMemberForm(String memberId, String nickname, String password,
        String confirmPassword, String email) {
        MemberForm memberForm = new MemberForm();
        memberForm.setMemberId(memberId);
        memberForm.setNickname(nickname);
        memberForm.setPassword(password);
        memberForm.setConfirmPassword(confirmPassword);
        memberForm.setEmail(email);
        return memberForm;
    }

    public static AnswerForm createAnswerForm(String content) {
        AnswerForm answerForm = new AnswerForm();
        answerForm.setContent(content);
        return answerForm;
    }

    public static Answer createAnswer(AnswerForm answerForm, Question question) {
        return Answer.builder()
            .id(1L)
            .content(answerForm.getContent())
            .createdAt(LocalDateTime.now())
            .question(question)
            .author(Member.builder()
                .id(1L)
                .memberId("onlyTest")
                .nickname("onlyTest")
                .password("onlyTest")
                .email("onlyTest@gmail.com")
                .build())
            .voter(new HashSet<>())
            .comments(new ArrayList<>())
            .build();
    }

    public static MemberDto createMember() {
        return MemberDto.builder()
            .id(1L)
            .memberId("onlyTest")
            .nickname("onlyTest")
            .email("onlyTest@test.com")
            .build();
    }
}
