package com.ll.pratice1.domain.category;

import com.ll.pratice1.domain.question.Question;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(unique = true)
    String category;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    private List<Question> questionList;
}
