package moaboa.auth.oauth2.userinfo;

import lombok.Builder;
import lombok.Getter;
import moaboa.auth.global.State;
import moaboa.auth.oauth2.SocialType;
import moaboa.auth.member.Role;
import moaboa.auth.member.Member;

import java.util.Map;

@Getter
public class OAuthAttributes {

    private String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
    private OAuth2UserInfo oauth2UserInfo; // 소셜 타입별 로그인 유저 정보(닉네임, 이메일, 프로필 사진 등등)

    @Builder
    private OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    /**
     * SocialType에 맞는 메소드 호출하여 OAuthAttributes 객체 반환
     * 파라미터 : userNameAttribute -> OAuth2 로그인 시 키(PK)가 되는 값 / attributes : OAuth 서비스의 유저 정보들
     * 소셜별 of 메소드(ofGoogle, ofKaKao, ofNaver)들은 각각 소셜 로그인 API에서 제공하는
     * 회원의 식별값(id), attributes, nameAttributeKey를 저장 후 build
     */
    public static OAuthAttributes of(SocialType socialType,
                                     String userNameAttribute,
                                     Map<String, Object> attributes) {
//        if (socialType == SocialType.NAVER) {
//            return ofNaver(userNameAttribute, attributes);
//        }
        if (socialType == SocialType.KAKAO) {
            return ofKakao(userNameAttribute, attributes);
        }
        return ofGoogle(userNameAttribute, attributes);
    }

    private static OAuthAttributes ofKakao(String userNameAttribute, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttribute)
                .oauth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofGoogle(String userNameAttribute, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttribute)
                .oauth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

//    public static OAuthAttributes ofNaver(String userNameAttribute, Map<String, Object> attributes) {
//        return OAuthAttributes.builder()
//                .nameAttributeKey(userNameAttribute)
//                .oauth2UserInfo(new NaverOAuth2User(attributes))
//                .build();
//    }

    /**
     * of메소드로 OAuthAttributes 객체가 생성되어, 유저 정보들이 담긴 OAuth2UserInfo가 소셜 타입별로 주입된 상태
     * role은 GUEST로 설정
     */
    public Member toEntity(SocialType socialType, OAuth2UserInfo oauth2UserInfo) {
        return Member.builder()
                .role(Role.GUEST)
                .socialType(socialType)
                .socialId(oauth2UserInfo.getId())
                .state(State.ACTIVE)
                .build();
    }
}
