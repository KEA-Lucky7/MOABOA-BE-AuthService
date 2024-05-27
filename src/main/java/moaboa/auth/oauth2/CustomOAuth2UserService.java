package moaboa.auth.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moaboa.auth.oauth2.userinfo.CustomOAuth2User;
import moaboa.auth.oauth2.userinfo.OAuthAttributes;
import moaboa.auth.member.Member;
import moaboa.auth.member.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static moaboa.auth.oauth2.SocialType.KAKAO;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        /**
         * userRequest에서 registrationId 추출 후 registrationId으로 SocialType 저장
         * http://localhost:8080/oauth2/authorization/kakao에서 kakao가 registrationId
         * userNameAttributeName은 이후에 nameAttributeKey로 설정된다.
         * nameAttributeKey 없이는 OAuth 구현 불가. 필요한 값
         */
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(); // OAuth2 로그인 시 키(PK)가 되는 값
        Map<String, Object> attributes = oAuth2User.getAttributes(); // 소셜 로그인에서 API가 제공. attributes에 제공받은 정보들이 있다

        // socialType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
        OAuthAttributes extractAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);
        Member createdMember = findUser(socialType, extractAttributes);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("GUEST")),
                attributes,
                extractAttributes.getNameAttributeKey(),
                createdMember.getSocialId(),
                createdMember.getSocialType()
        );
    }

    private SocialType getSocialType(String registrationId) {
//        if (NAVER.equals(registrationId)) {
//            return SocialType.NAVER;
//        }
        if (KAKAO.getName().equals(registrationId)) {
            return KAKAO;
        }
        return SocialType.GOOGLE;
    }

    private Member findUser(SocialType socialType, OAuthAttributes attributes) {
        Optional<Member> optionalUser = memberRepository.findBySocialTypeAndSocialId(socialType, attributes.getOauth2UserInfo().getId());
        return optionalUser.orElseGet(() -> saveUser(socialType, attributes));
    }

    private Member saveUser(SocialType socialType, OAuthAttributes attributes) {
        log.info("게스트 생성");
        return memberRepository.save(attributes.toEntity(socialType, attributes.getOauth2UserInfo()));
    }
}
