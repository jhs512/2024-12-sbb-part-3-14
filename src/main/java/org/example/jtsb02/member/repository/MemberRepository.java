package org.example.jtsb02.member.repository;

import java.util.Optional;
import org.example.jtsb02.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberId(String memberId);
}