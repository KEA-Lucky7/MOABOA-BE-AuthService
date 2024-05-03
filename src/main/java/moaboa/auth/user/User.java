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

    private String email; // 이메일
    private String password; // 비밀번호
    private String nickname; // 닉네임
    private String imageUrl; // 프로필 이미지

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;
}
