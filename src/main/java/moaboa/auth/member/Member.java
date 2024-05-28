package moaboa.auth.member;

import jakarta.persistence.*;
import lombok.*;
import moaboa.auth.global.BaseEntity;
import moaboa.auth.global.State;
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
    @Column(nullable = false)
    private SocialType socialType;

    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private LocalDate birth;
    private String profileImage;
    private String about;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;

}
