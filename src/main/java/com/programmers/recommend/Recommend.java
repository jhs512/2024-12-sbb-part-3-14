package com.programmers.recommend;

import com.programmers.article.Article;
import com.programmers.user.SiteUser;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Recommend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @OneToOne
    private Article article;

    @ManyToOne
    private SiteUser siteUser;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createDate;
}
