package baekgwa.sbb.model.question;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

    @EntityGraph(attributePaths = "answerList")
    @Query("SELECT q FROM Question q WHERE q.id = :id")
    Optional<Question> findByIdWithAnswers(@Param("id") Integer id);
}
