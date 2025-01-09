package com.programmers.article;

import com.programmers.comment.Comment;
import com.programmers.recommend.Recommend;
import com.programmers.user.SiteUser;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Article{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false)
    private SiteUser siteUser;

    @OneToMany(mappedBy = "article")
    private Set<Recommend> recommendSet;

    @OneToMany(mappedBy = "targetArticle")
    private Set<Comment> commentSet;
}
