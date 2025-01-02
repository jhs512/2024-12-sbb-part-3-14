package org.example.jtsb02.question.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.jtsb02.answer.entity.Answer;
import org.example.jtsb02.category.entity.Category;
import org.example.jtsb02.comment.entity.Comment;
import org.example.jtsb02.member.entity.Member;
import org.example.jtsb02.question.dto.QuestionDto;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private int hits;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answers;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @ManyToMany
    private Set<Member> voter;

    public static Question of(String subject, String content, Member author, Category category) {
        return Question.builder()
            .subject(subject)
            .content(content)
            .createdAt(LocalDateTime.now())
            .hits(0)
            .answers(new ArrayList<>())
            .comments(new ArrayList<>())
            .author(author)
            .voter(new HashSet<>())
            .category(category)
            .build();
    }

    public static Question OnlyIdFromQuestionDto(QuestionDto questionDto) {
        return Question.builder().id(questionDto.getId()).build();
    }
}