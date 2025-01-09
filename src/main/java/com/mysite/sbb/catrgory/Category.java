package com.mysite.sbb.catrgory;
import java.time.LocalDateTime;
import java.util.List;
import com.mysite.sbb.qustion.Question;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @CreatedDate
    private LocalDateTime createDate;
    @OneToMany( cascade = CascadeType.REMOVE)
    List<Question> questionList;
}
