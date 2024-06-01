package moaboa.auth.member;

import jakarta.persistence.*;
import lombok.*;
import moaboa.auth.global.BaseEntity;
import moaboa.auth.global.State;
import moaboa.auth.member.dto.MemberRequestDto;
import moaboa.auth.oauth2.SocialType;

import java.time.LocalDate;

@Getter
@Entity
@Builder
@Table(name = "member")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Column(name = "social_id", nullable = false)
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    private LocalDate birth;

    @Column(name = "profile_image")
    private String profileImage;
    private String about;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;

    public static Member from(MemberRequestDto.CreateDto request) {
        return Member.builder()
                .nickname(request.getNickname())
                .socialId(request.getSocialId())
                .socialType(SocialType.KAKAO)
                .role(Role.MEMBER)
                .about("테스트 유저랍니다~")
                .birth(request.getBirthdate())
                .state(State.ACTIVE)
                .build();
    }
}
