package com.mysite.sbb.Answer;

import com.mysite.sbb.Comment.Comment;
import com.mysite.sbb.Question.Question;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Answer {
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private Question question;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteUser> voter;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();
    /*
    @Id  :
    해당 속성을 엔티티의 기본 키(Primary Key)로 지정합니다.
    기본 키는 테이블에서 각 데이터를 구분할 수 있는 고유한 값이어야 하며, 중복되면 안 됩니다.
    테이블의 행을 식별하는 데 사용됩니다.

    @GeneratedValue :
    기본 키 값을 자동으로 생성합니다
    strategy = GenerationType.IDENTITY:
    기본 키가 데이터베이스의 Auto Increment 기능에 의해 순차적으로 생성됩니다.
    특정 필드만 별도로 번호가 증가합니다.

    strategy 옵션 생략 시:
    모든 @GeneratedValue 애너테이션이 지정된 필드에 대해 번호가 차례로 생성됩니다.
    특정 순서가 깨질 수 있으므로, 고유 번호 생성 시 주로 GenerationType.IDENTITY를 사용합니다.

    @Column :
    엔티티의 속성을 테이블의 열(Column)로 매핑하며, 열의 세부 설정을 제공합니다.

    length:
    열의 길이를 지정합니다.
    예: @Column(length = 200) → 열의 길이를 200으로 제한.

    columnDefinition:
    열 데이터의 유형이나 성격을 정의합니다.
    예: @Column(columnDefinition = "TEXT") → 텍스트 데이터를 저장하며, 글자 수 제한이 없습니다.

     */

}