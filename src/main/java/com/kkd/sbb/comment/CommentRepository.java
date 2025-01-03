package com.kkd.sbb.comment;

import com.kkd.sbb.question.Question;
import com.kkd.sbb.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByQuestion(Question question);

    Page<Comment> findByAuthor(SiteUser user, Pageable pageable);
}
