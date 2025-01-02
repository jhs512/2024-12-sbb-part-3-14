package com.mysite.sbb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedDate                    // 생성 시점에만 값이 설정
    @Column(updatable = false)      // 수정 불가능 하게
    private LocalDateTime createDate;

    @LastModifiedDate               // 생성 및 수정 시점에 값이 설정
    private LocalDateTime modifyDate;
}
