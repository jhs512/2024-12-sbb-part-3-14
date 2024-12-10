package baekgwa.sbb.model.answer.persistence;

import baekgwa.sbb.model.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

}
