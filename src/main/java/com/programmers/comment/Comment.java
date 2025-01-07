package com.programmers.comment;

import com.programmers.article.Article;
import com.programmers.data.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
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
}
