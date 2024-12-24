package com.mysite.sbb.comment.entity;

import com.mysite.sbb.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Comment extends BaseEntity {
    // BaseEntity : id, 내용, 작성자, 생성일, 수정일

    private Integer questionId;

    private Integer parentId;
}
