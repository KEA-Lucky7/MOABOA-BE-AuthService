package moaboa.auth.oauth2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import moaboa.auth.oauth2.userinfo.CustomOAuth2User;

@Getter
@Builder
@AllArgsConstructor
public class SocialLoginUser {

    private String id;
    private SocialType socialType;

    public static SocialLoginUser from(CustomOAuth2User oAuth2User) {
        return SocialLoginUser.builder()
                .id(oAuth2User.getAttribute("sub"))
                .socialType(oAuth2User.getSocialType())
                .build();
    }
}
