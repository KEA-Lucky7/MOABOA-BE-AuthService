package moaboa.auth.oauth2.userinfo;

import lombok.Getter;
import moaboa.auth.oauth2.SocialType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private SocialType socialType;
    private String email;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            String email,
                            SocialType socialType) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.socialType = socialType;
    }
}
