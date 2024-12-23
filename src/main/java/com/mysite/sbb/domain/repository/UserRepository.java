package com.mysite.sbb.domain.repository;

import com.mysite.sbb.domain.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<SiteUser, Long> {
    Optional<SiteUser> findByusername(String username);
}
