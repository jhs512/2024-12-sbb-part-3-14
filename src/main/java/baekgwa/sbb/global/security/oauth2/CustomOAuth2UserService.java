package baekgwa.sbb.global.security.oauth2;

import baekgwa.sbb.global.exception.DataNotFoundException;
import baekgwa.sbb.model.oauth2.entity.OAuth2SiteUser;
import baekgwa.sbb.model.oauth2.persistence.OAuth2UserRepository;
import baekgwa.sbb.model.user.entity.SiteUser;
import baekgwa.sbb.model.user.entity.UserRole;
import baekgwa.sbb.model.user.persistence.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OAuth2UserRepository oAuth2UserRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = getOauth2Response(registrationId, oAuth2User);

        SiteUser findUser;
        try {
            findUser = userRepository.findByEmail(oAuth2Response.getEmail()).orElseThrow(
                    () -> new DataNotFoundException("없는 회원"));
        } catch (DataNotFoundException e) {
            throw new OAuth2AuthenticationException(
                    String.format("[%s]로 가입된 정보가 없습니다. 회원가입 페이지로 이동합니다.", oAuth2Response.getEmail()));
        }

        String uuid = oAuth2Response.getProvider() + ":" + oAuth2Response.getProviderId();
        OAuth2SiteUser findOAuth2User = oAuth2UserRepository.findByUuid(uuid).orElseGet(
                () -> {
                    OAuth2SiteUser oAuth2SiteUser = OAuth2SiteUser
                            .builder()
                            .siteUser(findUser)
                            .uuid(uuid)
                            .build();
                    return oAuth2UserRepository.save(oAuth2SiteUser);
                });

        List<GrantedAuthority> authorities = new ArrayList<>();
        if("admin".equals(findUser.getUsername())) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }

        return new CustomOAuth2User(findUser.getUsername(), authorities);
    }

    private OAuth2Response getOauth2Response(String registrationId, OAuth2User oAuth2User) {
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {
            //todo:
        }
        else {
            return null;
        }

        return oAuth2Response;
    }
}
