package moaboa.auth.oauth2.userinfo;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return String.valueOf(this.attributes.get("id"));
    }

    @Override
    public String getNickname() {
        return null;
    }

    @Override
    public String getImageUrl() {
        return null;
    }
}
