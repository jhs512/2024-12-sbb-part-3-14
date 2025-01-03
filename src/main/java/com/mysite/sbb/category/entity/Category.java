package com.mysite.sbb.category.entity;

import com.mysite.sbb.global.entity.BaseEntityId;
import com.mysite.sbb.question.entity.Question;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Category extends BaseEntityId {
    // BaseEntityId : id

    @Column(unique = true, length = 10)
    private String categoryName;

    @Column(unique = true, length = 10)
    private String categoryKorName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    private List<Question> questionList;
}
