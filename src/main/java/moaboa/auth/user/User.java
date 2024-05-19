package moaboa.auth.user;

import jakarta.persistence.*;
import lombok.*;
import moaboa.auth.oauth2.SocialType;

@Getter
@Entity
@Builder
@Table(name = "USER_TB")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

    @Enumerated(EnumType.STRING)
    private Role role;
}
