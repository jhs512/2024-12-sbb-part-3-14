package baekgwa.sbb.model.comment.persistence;

import baekgwa.sbb.model.comment.entity.Comment;
import baekgwa.sbb.model.question.entity.Question;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @EntityGraph(attributePaths = {"siteUser"})
    List<Comment> findByQuestion(Question question);
}
