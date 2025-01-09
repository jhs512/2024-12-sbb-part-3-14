package com.programmers.comment;

import com.programmers.article.Article;
import com.programmers.data.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Comment extends BaseEntity {
    @OneToOne
    private Article parentArticle;

    @ManyToOne
    private Article targetArticle;

    @Column(columnDefinition = "VARCHAR(30)", nullable = false)
    private String content;

    @LastModifiedDate
    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime lastModifiedAt;
}
