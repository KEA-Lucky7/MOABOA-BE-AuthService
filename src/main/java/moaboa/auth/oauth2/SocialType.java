package moaboa.auth.oauth2;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SocialType {
    KAKAO("kakao"),
    GOOGLE("google");
//    ,NAVER

    private final String name;

    public String getName() {
        return name;
    }
}
