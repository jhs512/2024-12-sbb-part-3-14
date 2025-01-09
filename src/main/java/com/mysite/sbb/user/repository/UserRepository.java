package com.mysite.sbb.user.repository;

import com.mysite.sbb.user.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;
import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Long> {


    Optional<SiteUser> findByUsername(String username);
}
