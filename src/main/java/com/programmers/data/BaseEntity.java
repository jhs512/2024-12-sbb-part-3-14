package com.programmers.data;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime lastModifiedDate;
}
