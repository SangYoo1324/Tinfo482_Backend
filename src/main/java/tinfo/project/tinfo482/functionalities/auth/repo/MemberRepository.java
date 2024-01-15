package tinfo.project.tinfo482.functionalities.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tinfo.project.tinfo482.functionalities.auth.entity.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String nickname);
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
}
