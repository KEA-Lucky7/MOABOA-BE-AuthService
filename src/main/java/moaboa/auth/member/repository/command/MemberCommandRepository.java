package moaboa.auth.member.repository.command;

import moaboa.auth.member.Member;
import moaboa.auth.oauth2.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberCommandRepository extends JpaRepository<Member, Long> {

    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String id);

    Optional<Member> findBySocialId(String socialId);
}
