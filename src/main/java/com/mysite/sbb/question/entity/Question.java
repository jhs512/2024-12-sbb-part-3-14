package com.mysite.sbb.question.entity;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.global.entity.BaseEntityVoter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Question extends BaseEntityVoter {
    // BaseEntityVoter : id, content, createDate, author, modifyDate, voter

    @Column(length = 200)
    private String subject;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;
}
