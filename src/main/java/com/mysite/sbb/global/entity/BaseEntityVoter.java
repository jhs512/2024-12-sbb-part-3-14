package com.mysite.sbb.global.entity;

import com.mysite.sbb.user.entity.SiteUser;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@MappedSuperclass
@Getter
@Setter
public class BaseEntityVoter extends BaseEntity{
    @ManyToMany
    private List<SiteUser> voter;
}
