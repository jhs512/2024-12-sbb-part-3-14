package baekgwa.sbb.model.answer.persistence;

import baekgwa.sbb.model.answer.entity.Answer;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    @EntityGraph(attributePaths = {"siteUser"})
    @Query("SELECT a FROM Answer a WHERE a.id = :id")
    Optional<Answer> findByIdWithSiteUser(@Param("id") Integer id);

    @EntityGraph(attributePaths = {"question"})
    @Query("SELECT a FROM Answer a WHERE a.id = :id")
    Optional<Answer> findByIdWithQuestion(@Param("id") Integer id);

    @EntityGraph(attributePaths = {"question", "siteUser"})
    @Query("SELECT a FROM Answer a WHERE a.id = :id")
    Optional<Answer> findByIdWithQuestionAndSiteUser(@Param("id") Integer id);

    @EntityGraph(attributePaths = {"voter", "question"})
    @Query("SELECT a FROM Answer a WHERE a.id = :id")
    Optional<Answer> findByIdWithVoterAndQuestion(@Param("id") Integer id);

    @Query("SELECT a FROM Answer a LEFT JOIN a.voter v WHERE a.question.id = :questionId " +
            "GROUP BY a.id, a.createDate ORDER BY COUNT(v) DESC, a.createDate DESC")
    Page<Answer> findByQuestionIdOrderByVoterCountDesc(@Param("questionId") Integer questionId, Pageable pageable);


}
