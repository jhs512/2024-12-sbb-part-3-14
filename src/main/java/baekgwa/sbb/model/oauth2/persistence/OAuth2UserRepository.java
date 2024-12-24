package baekgwa.sbb.model.oauth2.persistence;

import baekgwa.sbb.model.oauth2.entity.OAuth2SiteUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuth2UserRepository extends JpaRepository<OAuth2SiteUser, Integer> {

    Optional<OAuth2SiteUser> findByUuid(String uuid);
}
