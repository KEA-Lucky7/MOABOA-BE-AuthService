package moaboa.auth.member.repository.command;

import moaboa.auth.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberCommandRepository extends JpaRepository<Member, Long> {
}
