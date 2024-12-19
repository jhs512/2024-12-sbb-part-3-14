package com.mysite.sbb.answer;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

import com.mysite.sbb.qustion.Question;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "Text")
    private String content;

    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser user;
    @ManyToMany
    private Set<SiteUser> voterSet;


    @ManyToOne(fetch =FetchType.LAZY)
    private Answer parent;

    @OneToMany
    private List<Answer> child;

    public int getVoterCount(){
        return voterSet.size();
    }

}
