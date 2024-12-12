package baekgwa.sbb.model.user.persistence;

import baekgwa.sbb.model.user.entity.SiteUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<SiteUser, Long> {

    Optional<SiteUser> findByUsername(String username);
}
