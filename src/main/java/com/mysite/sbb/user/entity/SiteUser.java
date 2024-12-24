package com.mysite.sbb.user.entity;

import com.mysite.sbb.global.entity.BaseEntityId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser extends BaseEntityId {
    // BaseEntityId : id (no setter)

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;
}
