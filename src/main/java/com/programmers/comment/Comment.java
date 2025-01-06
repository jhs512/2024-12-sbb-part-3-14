package com.programmers.comment;

import com.programmers.article.Article;
import com.programmers.data.BaseEntity;
import com.programmers.user.SiteUser;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Comment extends BaseEntity {
    @OneToOne
    private Article article;

    @ManyToOne
    private SiteUser siteUser;
}
