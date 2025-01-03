package com.mysite.sbb.answer.entity;

import com.mysite.sbb.global.entity.BaseEntityVoter;
import com.mysite.sbb.question.entity.Question;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Answer extends BaseEntityVoter {
    // BaseEntityVoter : id, content, createDate, author, modifyDate, voter

    @ManyToOne
    private Question question;
}
