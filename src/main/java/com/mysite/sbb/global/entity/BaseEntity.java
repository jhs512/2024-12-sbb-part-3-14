package com.mysite.sbb.global.entity;

import com.mysite.sbb.user.entity.SiteUser;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity extends BaseEntityId{
    // BaseEntityId : id (no setter)

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @ManyToMany
    private List<SiteUser> voter;
}
