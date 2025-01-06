package com.programmers.article;

import com.programmers.data.BaseEntity;
import com.programmers.user.SiteUser;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Article extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false)
    private SiteUser siteUser;
}
