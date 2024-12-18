package baekgwa.sbb.model.question.persistence;

import baekgwa.sbb.model.question.entity.Question;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

    @EntityGraph(attributePaths = {"answerList", "answerList.siteUser", "answerList.voter",
            "siteUser", "voter"})
    @Query("SELECT q FROM Question q WHERE q.id = :id")
    Optional<Question> findByIdWithAnswersAndSiteUserAndVoter(@Param("id") Integer id);

    @EntityGraph(attributePaths = {"siteUser"})
    @Query("SELECT q FROM Question q WHERE q.id = :id")
    Optional<Question> findByIdWithSiteUser(@Param("id") Integer id);

    @EntityGraph(attributePaths = {"siteUser", "voter"})
    @Query("SELECT q FROM Question q WHERE q.id = :id")
    Optional<Question> findByIdWithSiteUserAndVoter(@Param("id") Integer id);

    @EntityGraph(attributePaths = {"answerList", "siteUser"})
    Page<Question> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"voter"})
    @Query("SELECT q FROM Question q WHERE q.id = :id")
    Optional<Question> findByIdWithVoter(@Param("id") Integer id);

    Page<Question> findAll(Specification<Question> spec, Pageable pageable);
}
