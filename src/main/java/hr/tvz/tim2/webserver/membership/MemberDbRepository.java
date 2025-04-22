package hr.tvz.tim2.webserver.membership;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberDbRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByUser_Id(Long userId);
}
